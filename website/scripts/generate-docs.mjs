#!/usr/bin/env node
/**
 * Generates Docusaurus documentation pages for detekt rules by parsing
 * Kotlin source files from rule modules. Replaces the Gradle-based
 * detekt-generator for website builds, enabling pure Node.js builds for
 * Cloudflare Pages (including fork PR previews).
 */

import { readFileSync, writeFileSync, mkdirSync, readdirSync, statSync, existsSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT_DIR = join(__dirname, '../..');
const WEBSITE_DIR = join(__dirname, '..');
const OUTPUT_DIR = join(WEBSITE_DIR, 'docs/rules');

// ─── Version lookups ──────────────────────────────────────────────────────────

const libsVersions = readFileSync(join(ROOT_DIR, 'gradle/libs.versions.toml'), 'utf8');
const ktlintVersion = libsVersions.match(/^ktlint\s*=\s*"([^"]+)"/m)?.[1] ?? '';
const TEXT_REPLACEMENTS = { '<ktlintVersion/>': ktlintVersion };

// ─── Module discovery ─────────────────────────────────────────────────────────

const RULE_MODULES = [
  'detekt-rules-comments',
  'detekt-rules-complexity',
  'detekt-rules-coroutines',
  'detekt-rules-empty-blocks',
  'detekt-rules-exceptions',
  'detekt-rules-ktlint-wrapper',
  'detekt-rules-libraries',
  'detekt-rules-naming',
  'detekt-rules-performance',
  'detekt-rules-potential-bugs',
  'detekt-rules-ruleauthors',
  'detekt-rules-style',
];

function findKtFiles(dir) {
  const results = [];
  if (!existsSync(dir)) return results;
  for (const entry of readdirSync(dir)) {
    const full = join(dir, entry);
    if (statSync(full).isDirectory()) results.push(...findKtFiles(full));
    else if (entry.endsWith('.kt')) results.push(full);
  }
  return results;
}

// ─── Comment stripping ────────────────────────────────────────────────────────

// Returns a copy of `src` where all block comments are replaced with whitespace
// (preserving positions and newlines). Handles Kotlin nested block comments.
function stripBlockComments(src) {
  let result = '';
  let i = 0;
  let depth = 0; // nesting depth (Kotlin allows nested block comments)
  while (i < src.length) {
    if (depth > 0) {
      if (src.startsWith('/*', i)) { result += '  '; i += 2; depth++; }
      else if (src.startsWith('*/', i)) { result += '  '; i += 2; depth--; }
      else { result += src[i] === '\n' ? '\n' : ' '; i++; }
    } else {
      if (src.startsWith('/*', i)) { result += '  '; i += 2; depth++; }
      else { result += src[i]; i++; }
    }
  }
  return result;
}

// ─── Brace-aware extraction ───────────────────────────────────────────────────

/** Return the index of the closing brace matching the opening brace at `openIdx`. */
function findMatchingBrace(src, openIdx) {
  let depth = 0;
  let inString = false;
  let inTriple = false;
  let i = openIdx;
  while (i < src.length) {
    const ch = src[i];
    if (inTriple) {
      // Kotlin raw strings can contain `"` characters and even `""`; the
      // closing `"""` is the LAST `"""` in a run of quotes. So consume the
      // whole run when we see `"""` — single/double `"` chars stay as content.
      if (src.startsWith('"""', i)) {
        let q = 0;
        while (i + q < src.length && src[i + q] === '"') q++;
        inTriple = false;
        i += q;
        continue;
      }
    } else if (inString) {
      if (ch === '\\') { i += 2; continue; }
      if (ch === '"') inString = false;
    } else {
      if (src.startsWith('"""', i)) { inTriple = true; i += 3; continue; }
      if (ch === '"') { inString = true; }
      else if (ch === '{') { depth++; }
      else if (ch === '}') { depth--; if (depth === 0) return i; }
    }
    i++;
  }
  return -1;
}

/** Return the index of the closing paren matching the opening paren at `openIdx`. */
function findMatchingParen(src, openIdx) {
  let depth = 0;
  let inString = false;
  let inTriple = false;
  let i = openIdx;
  while (i < src.length) {
    const ch = src[i];
    if (inTriple) {
      // Kotlin raw strings can contain `"` characters and even `""`; the
      // closing `"""` is the LAST `"""` in a run of quotes. So consume the
      // whole run when we see `"""` — single/double `"` chars stay as content.
      if (src.startsWith('"""', i)) {
        let q = 0;
        while (i + q < src.length && src[i + q] === '"') q++;
        inTriple = false;
        i += q;
        continue;
      }
    } else if (inString) {
      if (ch === '\\') { i += 2; continue; }
      if (ch === '"') inString = false;
    } else {
      if (src.startsWith('"""', i)) { inTriple = true; i += 3; continue; }
      if (ch === '"') { inString = true; }
      else if (ch === '(') { depth++; }
      else if (ch === ')') { depth--; if (depth === 0) return i; }
    }
    i++;
  }
  return -1;
}

// ─── KDoc parsing ─────────────────────────────────────────────────────────────

function extractKDocBefore(src, classIdx) {
  const before = src.substring(0, classIdx);
  const endIdx = before.lastIndexOf('*/');
  if (endIdx === -1) return '';

  // Scan forward to find the outermost /** that pairs with the */ at endIdx.
  // lastIndexOf('/**') is wrong when code examples inside the KDoc contain
  // their own nested /** ... */ blocks (e.g. OutdatedDocumentation, ForbiddenComment).
  let startIdx = -1;
  let depth = 0;
  let i = 0;
  while (i < endIdx) {
    if (src.startsWith('/*', i)) {
      if (depth === 0) startIdx = i;
      depth++;
      i += 2;
    } else if (src.startsWith('*/', i)) {
      depth--;
      if (depth === 0) startIdx = -1; // this block closed before endIdx — reset
      i += 2;
    } else {
      i++;
    }
  }

  if (startIdx === -1 || !src.startsWith('/**', startIdx)) return '';
  // Only whitespace, annotations, or class modifiers may sit between the KDoc
  // and the `class` keyword. Without the modifier carve-out, `internal class`
  // wrappers (most of detekt-rules-ktlint-wrapper) silently lose their KDoc
  // and fall back to the `TODO: Specify description` placeholder.
  const gap = before.substring(endIdx + 2);
  const MODIFIERS = /\b(?:public|private|protected|internal|open|final|abstract|sealed|data|enum|inner|annotation|companion|value)\b/g;
  const cleanedGap = gap.replace(/@\w+(?:\([^)]*\))?/g, '').replace(MODIFIERS, '').trim();
  if (cleanedGap) return '';
  return src.substring(startIdx, endIdx + 2);
}

function cleanKDoc(raw) {
  return raw
    .replace(/^\/\*\*/, '')
    .replace(/\*\/$/, '')
    .split('\n')
    .map(line => {
      const t = line.trimStart();
      if (t.startsWith('* ')) return line.substring(line.indexOf('*') + 2);
      if (t === '*') return '';
      if (t.startsWith('*')) return line.substring(line.indexOf('*') + 1);
      return t;
    })
    .join('\n');
}

function parseKDoc(raw) {
  if (!raw) return { description: '', nonCompliant: '', compliant: '' };

  const cleaned = cleanKDoc(raw).trim().replace(/@@/g, '@');
  const NC_OPEN = '<noncompliant>';
  const NC_CLOSE = '</noncompliant>';
  const C_OPEN = '<compliant>';
  const C_CLOSE = '</compliant>';

  const ncStart = cleaned.indexOf(NC_OPEN);
  if (ncStart === -1) return { description: cleaned, nonCompliant: '', compliant: '' };

  const ncEnd = cleaned.indexOf(NC_CLOSE);
  const description = cleaned.substring(0, ncStart).trim();
  const nonCompliant = cleaned.substring(ncStart + NC_OPEN.length, ncEnd).replace(/^\n+/, '').trimEnd();

  const cStart = cleaned.indexOf(C_OPEN);
  let compliant = '';
  if (cStart !== -1) {
    const cEnd = cleaned.indexOf(C_CLOSE);
    compliant = cleaned.substring(cStart + C_OPEN.length, cEnd).replace(/^\n+/, '').trimEnd();
  }
  return { description, nonCompliant, compliant };
}

function applyReplacements(text) {
  let r = text;
  for (const [k, v] of Object.entries(TEXT_REPLACEMENTS)) r = r.split(k).join(v);
  return r;
}

// ─── Default value parsing ────────────────────────────────────────────────────

/**
 * Represents a parsed default value for printing.
 * type: 'string' | 'boolean' | 'int' | 'stringList' | 'valuesWithReason' | 'unknown'
 */
function parseLiteral(s) {
  s = s.trim();
  if (s === 'true') return { type: 'boolean', raw: 'true' };
  if (s === 'false') return { type: 'boolean', raw: 'false' };
  if (/^-?\d[\d_]*$/.test(s)) return { type: 'int', raw: String(parseInt(s.replace(/_/g, ''), 10)) };
  const strMatch = s.match(/^"((?:[^"\\]|\\.)*)"$/);
  if (strMatch) return { type: 'string', raw: strMatch[1] };
  const tripleMatch = s.match(/^"""([\s\S]*)"""$/);
  if (tripleMatch) return { type: 'string', raw: tripleMatch[1] };
  return null;
}

function formatDefault(v) {
  if (!v) return "''";
  switch (v.type) {
    case 'boolean': return v.raw;
    case 'int': return v.raw;
    case 'string': return `'${v.raw}'`;
    // Kotlin List.toString() gives [item1, item2] with brackets
    case 'stringList': return '[' + v.items.map(i => `'${i}'`).join(', ') + ']';
    case 'valuesWithReason': return '[' + v.keys.map(k => `'${k}'`).join(', ') + ']';
    default: return `'${v.raw}'`;
  }
}

/** Extract the raw content of balanced parens starting at `openIdx` in `src`. */
function parenContent(src, openIdx) {
  const close = findMatchingParen(src, openIdx);
  return close === -1 ? '' : src.substring(openIdx + 1, close).trim();
}

/**
 * Resolve string concatenation like `"a" + "b"` → `"ab"`.
 * Strips surrounding quotes and joins.
 */
function resolveStringConcatenation(expr) {
  // Split on `" + "`, `" +\n"` etc., unquote each part
  const parts = expr.split(/"[ \t]*\+[ \t\r\n]+"/).map(p => p.replace(/^"|"$/g, ''));
  return parts.join('');
}

/**
 * Parse a `listOf("a", "b")` or `emptyList()` expression.
 * Returns { type: 'stringList', items: [...] }
 */
function parseListExpr(expr) {
  expr = expr.trim();
  if (/^emptyList[(<]/.test(expr)) return { type: 'stringList', items: [] };
  const parenIdx = expr.indexOf('(');
  if (parenIdx === -1) return { type: 'stringList', items: [] };
  const inner = parenContent(expr, parenIdx);
  if (!inner) return { type: 'stringList', items: [] };
  const items = splitTopLevelCommas(inner)
    .map(s => s.trim().replace(/^"|"$/g, ''));
  return { type: 'stringList', items };
}

/**
 * Parse a `valuesWithReason("key1" to "reason1", ...)` expression.
 * Returns { type: 'valuesWithReason', keys: [...] }
 */
function parseValuesWithReasonExpr(expr) {
  const parenIdx = expr.indexOf('(');
  if (parenIdx === -1) return { type: 'valuesWithReason', keys: [] };
  const inner = parenContent(expr, parenIdx);
  if (!inner) return { type: 'valuesWithReason', keys: [] };
  const pairs = splitTopLevelCommas(inner);
  const keys = pairs
    .map(pair => {
      // Each pair is `"key" to "reason"` where reason may be multi-line concat
      const toIdx = pair.search(/\bto\b/);
      if (toIdx === -1) return null;
      const keyPart = pair.substring(0, toIdx).trim();
      // keyPart is `"key"` potentially with concatenation
      const strMatch = keyPart.match(/^"((?:[^"\\]|\\.)*)"/);
      return strMatch ? strMatch[1] : null;
    })
    .filter(Boolean);
  return { type: 'valuesWithReason', keys };
}

/** Split a comma-separated string at the top level (not inside parens/brackets/braces/strings). */
function splitTopLevelCommas(src) {
  const parts = [];
  let depth = 0;
  let inString = false;
  let inTriple = false;
  let start = 0;
  let i = 0;
  while (i < src.length) {
    const ch = src[i];
    if (inTriple) {
      // Kotlin raw strings can contain `"` characters and even `""`; the
      // closing `"""` is the LAST `"""` in a run of quotes. So consume the
      // whole run when we see `"""` — single/double `"` chars stay as content.
      if (src.startsWith('"""', i)) {
        let q = 0;
        while (i + q < src.length && src[i + q] === '"') q++;
        inTriple = false;
        i += q;
        continue;
      }
    } else if (inString) {
      if (ch === '\\') { i += 2; continue; }
      if (ch === '"') inString = false;
    } else {
      if (src.startsWith('"""', i)) { inTriple = true; i += 3; continue; }
      if (ch === '"') inString = true;
      else if ('([{'.includes(ch)) depth++;
      else if (')]}'.includes(ch)) depth--;
      else if (ch === ',' && depth === 0) {
        parts.push(src.substring(start, i));
        start = i + 1;
      }
    }
    i++;
  }
  if (start < src.length) parts.push(src.substring(start));
  return parts.filter(p => p.trim());
}

/**
 * Parse a Kotlin default value expression.
 * Returns a value object with type + display info.
 */
function parseDefaultExpr(expr, constants = {}) {
  expr = expr.trim();
  // Remove trailing lambda `{ ... }` used for transformation
  const lambdaMatch = expr.match(/^([\s\S]+?)\s*\{[\s\S]*\}$/);
  if (lambdaMatch) {
    // Only strip if braces are balanced (the outer ones are the lambda)
    const candidate = lambdaMatch[1].trim();
    expr = candidate || expr;
  }

  // listOf / emptyList
  if (/^(?:listOf|emptyList)[(<]/.test(expr)) return parseListExpr(expr);
  // valuesWithReason
  if (expr.startsWith('valuesWithReason(')) return parseValuesWithReasonExpr(expr);
  // String concatenation: `"a" + "b"`
  if (/"[ \t]*\+[ \t\r\n]*"/.test(expr)) {
    return { type: 'string', raw: resolveStringConcatenation(expr) };
  }
  // Literal
  const lit = parseLiteral(expr);
  if (lit) return lit;
  // Constant reference
  if (/^\w+$/.test(expr) && constants[expr] !== undefined) return constants[expr];
  // Unknown — wrap as string to avoid breaking output
  return { type: 'string', raw: expr };
}

// ─── Companion object constant extraction ─────────────────────────────────────

function extractCompanionConstants(classBody) {
  const constants = {};
  const compMatch = classBody.match(/\bcompanion\s+object\b[^{]*/);
  if (!compMatch) return constants;
  const compOpenIdx = classBody.indexOf('{', compMatch.index + compMatch[0].length);
  if (compOpenIdx === -1) return constants;
  const compCloseIdx = findMatchingBrace(classBody, compOpenIdx);
  const compBody = classBody.substring(compOpenIdx + 1, compCloseIdx);

  // Match the start of `[const ]val NAME = `. The value itself may span
  // multiple lines (e.g. `listOf("a", "b", ...)` with each item on its own
  // line), so we scan from the `=` to the end of the expression instead of
  // capturing the rest of the line with a regex.
  const re = /(?:const\s+)?val\s+(\w+)\s*=\s*/g;
  let m;
  while ((m = re.exec(compBody)) !== null) {
    const name = m[1];
    const valueExpr = readBalancedExpression(compBody, m.index + m[0].length);
    re.lastIndex = m.index + m[0].length + valueExpr.length;
    const cleaned = valueExpr.split('//')[0].trim();
    const parsed = parseDefaultExpr(cleaned, constants);
    if (parsed) constants[name] = parsed;
  }
  return constants;
}

// Read an expression starting at `start` until we hit a top-level newline or
// the end. Skips over balanced (), [], {} and string literals, so a multi-line
// `listOf("a", "b")` reads as one expression.
function readBalancedExpression(src, start) {
  let i = start;
  let depth = 0;
  let inString = null;
  while (i < src.length) {
    const ch = src[i];
    if (inString) {
      if (ch === '\\') { i += 2; continue; }
      if (ch === inString) inString = null;
    } else if (ch === '"' || ch === "'") {
      inString = ch;
    } else if (ch === '(' || ch === '[' || ch === '{') {
      depth++;
    } else if (ch === ')' || ch === ']' || ch === '}') {
      if (depth === 0) break;
      depth--;
    } else if (ch === '\n' && depth === 0) {
      break;
    }
    i++;
  }
  return src.substring(start, i);
}

// ─── @Configuration property extraction ──────────────────────────────────────

/**
 * Extract the string content of a `@Configuration("...")` annotation.
 * Handles single-line, multi-line, and concatenated strings.
 */
function extractConfigAnnotationDesc(src, annotationIdx) {
  const parenIdx = src.indexOf('(', annotationIdx);
  if (parenIdx === -1) return '';
  const closeIdx = findMatchingParen(src, parenIdx);
  const raw = src.substring(parenIdx + 1, closeIdx).trim();
  // Remove surrounding quotes and handle concatenation
  const unquoted = raw.replace(/^"|"$/g, '').replace(/"[ \t]*\+[ \t\r\n]*"/g, '');
  return unquoted.trim();
}

/**
 * Parse all `@Configuration` properties from a class body.
 * Returns an array of { name, description, defaultValue, androidDefaultValue? }.
 */
function parseConfigurations(classBody, companionConstants = {}, forRuleSet = false) {
  const configs = [];
  // Find all @Configuration annotations
  const re = /@Configuration\b/g;
  let m;
  while ((m = re.exec(classBody)) !== null) {
    const annotationIdx = m.index;
    const description = extractConfigAnnotationDesc(classBody, annotationIdx);

    // Skip past the annotation to find `val propName ... by delegateName(...)`
    const afterAnnotation = classBody.indexOf('\n', annotationIdx);
    // Match any `by <identifier>(` — handles config, configWithAndroidVariants, ruleSetConfig, etc.
    const byDelegateRe = /\bby\s+(\w+)\s*\(/g;
    byDelegateRe.lastIndex = afterAnnotation > -1 ? afterAnnotation : annotationIdx;
    const byMatch = byDelegateRe.exec(classBody);
    if (!byMatch) continue;

    const delegateName = byMatch[1];
    const parenOpen = byMatch.index + byMatch[0].length - 1;
    const parenClose = findMatchingParen(classBody, parenOpen);
    const argsRaw = classBody.substring(parenOpen + 1, parenClose);

    // Extract property name from `val propName` between annotation and delegate
    const segment = classBody.substring(annotationIdx, byMatch.index);
    const propNameMatch = segment.match(/\bval\s+(\w+)\b/);
    if (!propNameMatch) continue;
    const propName = propNameMatch[1];

    // Deprecated annotation on same property?
    const deprecatedMatch = segment.match(/@Deprecated\s*\(\s*"([^"]*)"\s*\)/);
    const deprecated = deprecatedMatch ? deprecatedMatch[1] : null;

    let defaultValue, androidDefaultValue;

    if (delegateName === 'configWithAndroidVariants') {
      const args = splitTopLevelCommas(argsRaw);
      const rawDefault = (args[0] ?? '').replace(/^\s*defaultValue\s*=\s*/, '').trim();
      const rawAndroid = (args[1] ?? '').replace(/^\s*defaultAndroidValue\s*=\s*/, '').trim();
      defaultValue = parseDefaultExpr(rawDefault, companionConstants);
      androidDefaultValue = args[1] ? parseDefaultExpr(rawAndroid, companionConstants) : null;
    } else if (delegateName === 'configWithFallback') {
      const args = splitTopLevelCommas(argsRaw);
      const defaultArg = args.find(a => !a.trim().startsWith('::') && !a.includes('fallbackProperty'));
      const rawVal = defaultArg?.replace(/\bdefaultValue\s*=\s*/, '').trim() ?? '';
      defaultValue = parseDefaultExpr(rawVal, companionConstants);
    } else {
      // config(defaultValue), ruleSetConfig(defaultValue), or any other delegate.
      // Strip the `defaultValue = ` named-argument prefix when present so we
      // parse the value itself, not the named-arg syntax.
      const args = splitTopLevelCommas(argsRaw);
      const rawVal = (args[0] ?? '').replace(/^\s*defaultValue\s*=\s*/, '').trim();
      defaultValue = parseDefaultExpr(rawVal, companionConstants);
    }

    // For rule set provider configs, android default = default (same value, always shown)
    if (forRuleSet) androidDefaultValue = defaultValue;

    configs.push({ name: propName, description, defaultValue, androidDefaultValue, deprecated });
  }
  return configs;
}

// ─── Annotation extraction helpers ───────────────────────────────────────────

function getAnnotationParam(src, fromIdx, toIdx, annotationName) {
  const block = src.substring(fromIdx, toIdx);
  const re = new RegExp(`@${annotationName}\\s*\\(`);
  const m = re.exec(block);
  if (!m) return null;
  const parenOpen = block.indexOf('(', m.index);
  const parenClose = findMatchingParen(block, parenOpen);
  return block.substring(parenOpen + 1, parenClose).trim();
}

function extractAnnotationFirstArg(src, fromIdx, toIdx, annotationName) {
  const raw = getAnnotationParam(src, fromIdx, toIdx, annotationName);
  if (!raw) return null;
  // Handle `since = "1.0.0"` or `"1.0.0"`
  const withoutNamed = raw.replace(/^\s*\w+\s*=\s*/, '');
  return withoutNamed.replace(/^"|"$/g, '');
}

function hasAnnotation(src, fromIdx, toIdx, annotationName) {
  const block = src.substring(fromIdx, toIdx);
  return new RegExp(`@${annotationName}\\b`).test(block);
}

// ─── Top-level file parsing ───────────────────────────────────────────────────

const RULE_SUPERTYPES = ['Rule', 'KtlintRule', 'EmptyRule'];
const PROVIDER_SUPERTYPES = ['DefaultRuleSetProvider', 'RuleSetProvider'];


function parseKtFile(content, filePath) {
  // Strip block comments so we don't match `class Foo` inside KDoc examples.
  // Positions in `stripped` exactly match positions in `content`.
  const stripped = stripBlockComments(content);

  // Find all top-level class declarations (search in stripped source, positions match original)
  const classRe = /\bclass\s+(\w+)/g;
  let m;
  while ((m = classRe.exec(stripped)) !== null) {
    const className = m[1];
    const classIdx = m.index;

    // Find the class opening brace starting from classIdx in `content`.
    // Must be string-aware to ignore parens inside string literals.
    let braceIdx = -1;
    {
      let i = classIdx + m[0].length;
      let parenDepth = 0;
      let inStr = false;
      let inTripleStr = false;
      while (i < content.length) {
        const ch = content[i];
        if (inTripleStr) {
          if (content.startsWith('"""', i)) { inTripleStr = false; i += 3; continue; }
        } else if (inStr) {
          if (ch === '\\') { i += 2; continue; }
          if (ch === '"') inStr = false;
        } else {
          if (content.startsWith('"""', i)) { inTripleStr = true; i += 3; continue; }
          if (ch === '"') { inStr = true; }
          else if (ch === '(') parenDepth++;
          else if (ch === ')') parenDepth--;
          else if (ch === '{' && parenDepth === 0) { braceIdx = i; break; }
        }
        i++;
      }
    }
    if (braceIdx === -1) continue;

    const header = content.substring(classIdx, braceIdx);

    // Classify the class
    const isRule = RULE_SUPERTYPES.some(st => new RegExp(`\\b${st}\\s*[(,]`).test(header));
    const isProvider = PROVIDER_SUPERTYPES.some(st => new RegExp(`\\b${st}\\b`).test(header));

    if (!isRule && !isProvider) continue;

    const closeIdx = findMatchingBrace(content, braceIdx);
    const classBody = content.substring(braceIdx + 1, closeIdx);

    // Extract KDoc
    const rawKDoc = extractKDocBefore(content, classIdx);
    const { description, nonCompliant, compliant } = parseKDoc(rawKDoc);

    // Annotations between KDoc end and class keyword
    const kdocEnd = rawKDoc ? content.lastIndexOf('*/', classIdx) + 2 : classIdx;
    const annotationsBlock = content.substring(kdocEnd, classIdx);

    if (isProvider) {
      return parseProvider(content, className, classBody, description, annotationsBlock);
    } else {
      return parseRule(content, className, classBody, header, description, nonCompliant, compliant, annotationsBlock);
    }
  }
  return null;
}

function parseProvider(content, className, classBody, description, annotationsBlock) {
  // Extract ruleSetId
  const idMatch = classBody.match(/\bruleSetId\s*=\s*RuleSetId\s*\(\s*"([^"]+)"\s*\)/);
  if (!idMatch) return null;
  const ruleSetId = idMatch[1];

  // Extract rule list from listOf(::Rule1, ::Rule2, ...)
  const ruleNames = [];
  const listOfMatch = classBody.match(/\blistOf\s*\(/);
  if (listOfMatch) {
    const listOpen = classBody.indexOf('(', listOfMatch.index + 'listOf'.length);
    const listClose = findMatchingParen(classBody, listOpen);
    const listContent = classBody.substring(listOpen + 1, listClose);
    const refs = listContent.match(/::\w+/g) ?? [];
    ruleNames.push(...refs.map(r => r.substring(2)));
  }

  // Active by default
  const activeParam = extractAnnotationFirstArg(annotationsBlock, 0, annotationsBlock.length, 'ActiveByDefault');
  const activeByDefault = activeParam ? { active: true, since: activeParam } : { active: false };

  // Rule-set-level configurations (in companion object or inline)
  const companionConstants = extractCompanionConstants(classBody);
  const configurations = parseConfigurations(classBody, companionConstants, true);

  return {
    type: 'provider',
    className,
    ruleSetId,
    description: applyReplacements(description),
    activeByDefault,
    ruleNames,
    configurations,
  };
}

function parseRule(content, className, classBody, header, description, nonCompliant, compliant, annotationsBlock) {
  // Active by default
  const activeParam = extractAnnotationFirstArg(annotationsBlock, 0, annotationsBlock.length, 'ActiveByDefault');
  const activeByDefault = activeParam ? { active: true, since: activeParam } : { active: false };

  // Deprecated
  const deprecatedParam = extractAnnotationFirstArg(annotationsBlock, 0, annotationsBlock.length, 'Deprecated');

  // Aliases
  const aliasBlock = getAnnotationParam(annotationsBlock, 0, annotationsBlock.length, 'Alias');
  const aliases = aliasBlock
    ? aliasBlock.match(/"([^"]+)"/g)?.map(s => s.replace(/"/g, '')) ?? []
    : [];

  // RequiresAnalysisApi (via interface or annotation)
  const requiresFullAnalysis =
    /\bRequiresAnalysisApi\b/.test(header) ||
    hasAnnotation(annotationsBlock, 0, annotationsBlock.length, 'RequiresAnalysisApi');

  // Configurations
  const companionConstants = extractCompanionConstants(classBody);
  const configurations = parseConfigurations(classBody, companionConstants, false);

  return {
    type: 'rule',
    className,
    description: applyReplacements(description),
    nonCompliant: applyReplacements(nonCompliant),
    compliant: applyReplacements(compliant),
    activeByDefault,
    deprecated: deprecatedParam ?? null,
    aliases,
    requiresFullAnalysis,
    configurations,
  };
}

// ─── Markdown generation ──────────────────────────────────────────────────────

/**
 * Replicate the `MarkdownContent.append()` builder behavior:
 * - joining sections with a single `\n` between them
 */
class MarkdownBuilder {
  #parts = [];
  append(text) { if (text) this.#parts.push(text); }
  paragraph(text) { this.append(text + '\n'); }
  h3(text) { this.append(`### ${text}\n`); }
  h4(text) { this.append(`#### ${text}\n`); }
  build() { return this.#parts.join('\n'); }
}

function bold(text) { return `**${text}**`; }
function crossOut(text) { return `~~${text}~~`; }
function code(text) { return `\`\`${text}\`\``; }
function codeBlock(content) { return `\`\`\`kotlin\n${content}\n\`\`\``; }

function printConfigurations(configs) {
  if (!configs.length) return '';
  const md = new MarkdownBuilder();
  md.h4('Configuration options:');

  // Replicate Kotlin's MarkdownList builder: each append() call is joined by '\n'.
  // item() → "* text\n", description() → "  text\n"
  const listParts = [];
  for (const c of configs) {
    const defVal = formatDefault(c.defaultValue);
    const andVal = c.androidDefaultValue ? formatDefault(c.androidDefaultValue) : null;
    const defStr = andVal !== null
      ? `(default: ${code(defVal)}) (android default: ${code(andVal)})`
      : `(default: ${code(defVal)})`;

    const nameDisplay = c.deprecated ? crossOut(code(c.name)) : code(c.name);
    listParts.push(`* ${nameDisplay} ${defStr}\n`);
    if (c.deprecated) {
      listParts.push(`  ${bold('Deprecated')}: ${c.deprecated}\n`);
    }
    listParts.push(`  ${c.description}\n`);
  }
  // Join with '\n' (MarkdownList.append separator), result ends with '\n'
  md.append(listParts.join('\n'));
  return md.build();
}

function printRule(rule) {
  const md = new MarkdownBuilder();

  if (rule.deprecated) {
    md.h3(crossOut(rule.className));
    md.paragraph(rule.deprecated);
  } else {
    md.h3(rule.className);
  }

  if (rule.description) {
    md.paragraph(rule.description);
  } else {
    md.paragraph('TODO: Specify description');
  }

  const activePart = rule.activeByDefault.active
    ? `${bold('Active by default')}: Yes - Since v${rule.activeByDefault.since}`
    : `${bold('Active by default')}: No`;
  md.paragraph(activePart);

  if (rule.requiresFullAnalysis) {
    md.paragraph(bold('Requires Type Resolution'));
  }

  if (rule.aliases.length > 0) {
    md.paragraph(`${bold('Aliases')}: ${rule.aliases.join(', ')}`);
  }

  const configsSection = printConfigurations(rule.configurations);
  if (configsSection) md.append(configsSection);

  if (rule.nonCompliant) {
    md.h4('Noncompliant Code:');
    md.paragraph(codeBlock(rule.nonCompliant));
  }
  if (rule.compliant) {
    md.h4('Compliant Code:');
    md.paragraph(codeBlock(rule.compliant));
  }

  return md.build();
}

function printRuleSetPage(provider, rules) {
  const md = new MarkdownBuilder();
  md.paragraph(`Rule Set ID: \`${provider.ruleSetId}\``);

  if (provider.description) {
    md.paragraph(provider.description);
  } else {
    md.paragraph('TODO: Specify description');
  }

  const configsSection = printConfigurations(provider.configurations);
  if (configsSection) md.append(configsSection);

  for (const rule of rules) {
    md.append(printRule(rule));
  }

  return md.build();
}

function makeHeader(ruleSetName) {
  const title = ruleSetName.charAt(0).toUpperCase() + ruleSetName.slice(1);
  return [
    '---',
    `title: ${title} Rule Set`,
    'sidebar: home_sidebar',
    `keywords: [rules, ${ruleSetName}]`,
    `permalink: ${ruleSetName}.html`,
    'toc: true',
    'folder: documentation',
    '---',
  ].join('\n');
}

function generateFile(provider, rules) {
  const header = makeHeader(provider.ruleSetId);
  const body = printRuleSetPage(provider, rules);
  return header + '\n' + body;
}

// ─── CLI options generation ───────────────────────────────────────────────────

// Parse enum entry names from a Kotlin enum class source file.
// Stops at the trailing `;` that separates entries from member declarations.
function parseEnumEntries(src) {
  const stripped = stripBlockComments(src).replace(/\/\/[^\n]*/g, '');
  const braceIdx = stripped.indexOf('{', stripped.indexOf('enum class'));
  if (braceIdx === -1) return [];
  const body = stripped.substring(braceIdx + 1);
  // Enum entry list ends at the first standalone `;` line
  const semiIdx = body.search(/^\s*;/m);
  const entriesSection = semiIdx !== -1 ? body.substring(0, semiIdx) : body;
  return (entriesSection.match(/^\s*([A-Za-z_]\w*)\s*(?:[,({]|$)/mg) ?? [])
    .map(m => m.trim().match(/^([A-Za-z_]\w*)/)?.[1])
    .filter(Boolean);
}

// Mirrors JCommander's DefaultUsageFormatter.wrapDescription so the JS-generated
// _cli-options.md is byte-identical to the Gradle one. Notable quirks:
//   * the input is the indent-prefixed string, split on a single space (not
//     collapsed whitespace), so leading spaces become empty "words";
//   * when a word is appended to the current line, the trailing space is only
//     added if it is NOT the last word — so non-wrapped final lines have no
//     trailing space;
//   * when a word is wrapped onto a new line, a trailing space is ALWAYS
//     appended, even for the last word — so wrapped final lines DO have a
//     trailing space.
function wordWrap(text, indent, maxWidth) {
  const indented = ' '.repeat(indent) + text;
  const words = indented.split(' ');
  let out = '';
  let current = 0;

  for (let i = 0; i < words.length; i++) {
    const word = words[i];

    if (word.length > maxWidth || current + 1 + word.length <= maxWidth) {
      out += word;
      current += word.length;
      if (i !== words.length - 1) {
        out += ' ';
        current++;
      }
    } else {
      out += '\n' + ' '.repeat(indent) + word + ' ';
      current = indent + word.length + 1;
    }
  }
  return out;
}

// Scan string literal(s) (possibly joined by +) starting at position i in src.
// Returns { value, end } where value is the concatenated string content.
function scanStringValue(src, i) {
  let value = '';
  while (i < src.length) {
    while (i < src.length && /\s/.test(src[i])) i++;
    if (src[i] !== '"') break;
    i++; // skip opening "
    while (i < src.length && src[i] !== '"') {
      if (src[i] === '\\') { value += src[i + 1]; i += 2; }
      else value += src[i++];
    }
    i++; // skip closing "
    // Check for + continuation (may have whitespace/newlines between)
    let j = i;
    while (j < src.length && /\s/.test(src[j])) j++;
    if (src[j] === '+') { i = j + 1; continue; }
    break;
  }
  return { value: value.trim(), end: i };
}

function generateCliOptionsFile() {
  const CLI_OPTIONS_OUTPUT = join(WEBSITE_DIR, 'docs/gettingstarted/_cli-options.md');

  const analysisModeEntries = parseEnumEntries(
    readFileSync(join(ROOT_DIR, 'detekt-tooling/src/main/kotlin/dev/detekt/tooling/api/AnalysisMode.kt'), 'utf8')
  );
  const failureSeverityEntries = parseEnumEntries(
    readFileSync(join(ROOT_DIR, 'detekt-cli/src/main/kotlin/dev/detekt/cli/FailureSeverity.kt'), 'utf8')
  );

  // JCommander emits "Possible Values" for enum-typed fields. JvmTarget and
  // LanguageVersion come from the Kotlin compiler dependency (not in local
  // source). ApiVersion is NOT an enum — it's a class with a static factory —
  // so JCommander emits no possible-values line for --api-version even though
  // it goes through ApiVersionConverter.
  const POSSIBLE_VALUES = {
    AnalysisMode: `[${analysisModeEntries.join(', ')}]`,
    FailureSeverityConverter: `[${failureSeverityEntries.join(', ')}]`,
    JvmTargetConverter: '[1.6, 1.8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26]',
    LanguageVersionConverter: '[1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5]',
  };

  const cliArgsSrc = readFileSync(
    join(ROOT_DIR, 'detekt-cli/src/main/kotlin/dev/detekt/cli/CliArgs.kt'), 'utf8'
  );

  const AT_PARAM = '@Parameter';
  const params = [];
  let searchFrom = 0;

  while (true) {
    const atIdx = cliArgsSrc.indexOf(AT_PARAM, searchFrom);
    if (atIdx === -1) break;
    const parenStart = cliArgsSrc.indexOf('(', atIdx + AT_PARAM.length);
    if (parenStart === -1) break;
    const parenEnd = findMatchingParen(cliArgsSrc, parenStart);
    if (parenEnd === -1) break;
    searchFrom = parenEnd + 1;

    const annotation = cliArgsSrc.substring(parenStart + 1, parenEnd);
    if (/\bhidden\s*=\s*true/.test(annotation)) continue;
    // JCommander suppresses the "Default:" line when @Parameter(help = true).
    const isHelp = /\bhelp\s*=\s*true/.test(annotation);

    // names = ["--foo", "-f"]
    const namesMatch = annotation.match(/names\s*=\s*\[([^\]]+)\]/);
    if (!namesMatch) continue;
    const names = (namesMatch[1].match(/"([^"]+)"/g) ?? []).map(s => s.replace(/"/g, ''));

    // description = "..." (possibly multi-line concatenation) — scan properly
    const descKeyIdx = annotation.search(/\bdescription\s*=/);
    let description = '';
    if (descKeyIdx !== -1) {
      const valueStart = annotation.indexOf('=', descKeyIdx) + 1;
      description = scanStringValue(annotation, valueStart).value;
    }

    // converter = FooConverter::class
    const converterMatch = annotation.match(/converter\s*=\s*(\w+)/);
    const converter = converterMatch ? converterMatch[1] : null;

    // var fieldName: FieldType = defaultExpr
    const afterAnnotation = cliArgsSrc.substring(parenEnd + 1);
    const varMatch = afterAnnotation.match(/^\s*(?:@\w+[^)]*\)\s*)*(?:var|val)\s+\w+\s*:\s*(\w+)[^=\n]*=\s*([^\n]+)/);
    const fieldType = varMatch ? varMatch[1] : null;
    const defaultExpr = varMatch ? varMatch[2].trim() : '';

    let defaultValue = null;
    if (defaultExpr === 'false' || defaultExpr === 'true') defaultValue = defaultExpr;
    else if (/^(?:emptyList|mutableListOf)\(\)/.test(defaultExpr)) defaultValue = '[]';
    else if (/AnalysisMode\.(\w+)/.test(defaultExpr)) defaultValue = defaultExpr.match(/AnalysisMode\.(\w+)/)[1];
    else if (/FailureSeverity\.(\w+)/.test(defaultExpr)) defaultValue = defaultExpr.match(/FailureSeverity\.(\w+)/)[1];
    else if (/JvmTarget\.DEFAULT/.test(defaultExpr)) defaultValue = '1.8';

    const possibleValues = POSSIBLE_VALUES[converter] ?? POSSIBLE_VALUES[fieldType] ?? null;
    const primaryName = names.find(n => n.startsWith('--')) ?? names[0];
    params.push({
      names,
      description,
      defaultValue: isHelp ? null : defaultValue,
      possibleValues,
      primaryName,
    });
  }

  params.sort((a, b) => a.primaryName.localeCompare(b.primaryName));

  let out = 'Usage: detekt [options] Options to pass to the Kotlin compiler.\n  Options:\n';
  for (const { names, description, defaultValue, possibleValues } of params) {
    out += `    ${names.join(', ')}\n`;
    if (description) out += wordWrap(description, 6, 79) + '\n';
    if (defaultValue !== null) out += `      Default: ${defaultValue}\n`;
    if (possibleValues !== null) out += `      Possible Values: ${possibleValues}\n`;
  }

  writeFileSync(CLI_OPTIONS_OUTPUT, '```\n' + out + '\n```\n', 'utf8');
  console.log(`  Wrote: ${CLI_OPTIONS_OUTPUT}`);
}

// ─── Main ─────────────────────────────────────────────────────────────────────

function main() {
  console.log('Generating detekt rule documentation...');

  const providers = new Map(); // ruleSetId → provider
  const rulesByName = new Map(); // className → rule
  const ruleOrder = new Map(); // ruleSetId → [className, ...]

  for (const moduleName of RULE_MODULES) {
    const srcDir = join(ROOT_DIR, moduleName, 'src/main/kotlin');
    const ktFiles = findKtFiles(srcDir);

    for (const filePath of ktFiles) {
      const content = readFileSync(filePath, 'utf8');
      const parsed = parseKtFile(content, filePath);
      if (!parsed) continue;

      if (parsed.type === 'provider') {
        providers.set(parsed.ruleSetId, parsed);
        ruleOrder.set(parsed.ruleSetId, parsed.ruleNames);
      } else if (parsed.type === 'rule') {
        rulesByName.set(parsed.className, parsed);
      }
    }
  }

  mkdirSync(OUTPUT_DIR, { recursive: true });

  let generated = 0;
  for (const [ruleSetId, provider] of providers) {
    // Sort by class name (code-point order) to match the Kotlin generator,
    // which uses `String.compareTo` — keeps the two outputs byte-identical
    // so the parity check in CI stays meaningful.
    const orderedNames = [...(ruleOrder.get(ruleSetId) ?? [])]
      .sort((a, b) => (a < b ? -1 : a > b ? 1 : 0));
    const rules = orderedNames
      .map(name => rulesByName.get(name))
      .filter(Boolean);

    const content = generateFile(provider, rules);
    const outPath = join(OUTPUT_DIR, `${ruleSetId}.md`);
    writeFileSync(outPath, content, 'utf8');
    console.log(`  Wrote: ${outPath}`);
    generated++;
  }

  console.log(`\nGenerated ${generated} rule set documentation files.`);
  generateCliOptionsFile();
}

main();
