---
title: Tables
tags: [formatting]
keywords: datatables, tables, grids, markdown, multimarkdown, jquery plugins
last_updated: July 16, 2016
datatable: true
summary: "You can format tables using either multimarkdown syntax or HTML. You can also use jQuery datatables (a plugin) if you need more robust tables."
sidebar: mydoc_sidebar
permalink: mydoc_tables.html
folder: mydoc
---

## Multimarkdown Tables

You can use Multimarkdown syntax for tables. The following shows a sample:

```
| Priority apples | Second priority | Third priority |
|-------|--------|---------|
| ambrosia | gala | red delicious |
| pink lady | jazz | macintosh |
| honeycrisp | granny smith | fuji |
```

**Result:**

| Priority apples | Second priority | Third priority |
|-------|--------|---------|
| ambrosia | gala | red delicious |
| pink lady | jazz | macintosh |
| honeycrisp | granny smith | fuji |

{% include note.html content="You can't use block level tags (paragraphs or lists) inside Markdown tables, so if you need separate paragraphs inside a cell, use `<br/><br/>`." %}

## HTML Tables {#htmltables}

If you need a more sophisticated table syntax, use HTML syntax for the table. Although you're using HTML, you can use Markdown inside the table cells by adding `markdown="span"` as an attribute for the `td` tag, as shown in the following table. You can also control the column widths.

```html
<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Field</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr>
<td markdown="span">First column **fields**</td>
<td markdown="span">Some descriptive text. This is a markdown link to [Google](http://google.com). Or see [some link][mydoc_tags].</td>
</tr>
<tr>
<td markdown="span">Second column **fields**</td>
<td markdown="span">Some more descriptive text.
</td>
</tr>
</tbody>
</table>
```

**Result:**
<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Field</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr>
<td markdown="span">First column **fields**</td>
<td markdown="span">Some descriptive text. This is a markdown link to [Google](http://google.com). Or see [some link][mydoc_tags].</td>
</tr>
<tr>
<td markdown="span">Second column **fields**</td>
<td markdown="span">Some more descriptive text.
</td>
</tr>
</tbody>
</table>

## jQuery DataTables

You also have the option of using a [jQuery DataTable](https://www.datatables.net/), which gives you some additional capabilities. To use a jQuery DataTable in a page, include `datatable: true` in a page's frontmatter. This tells the default layout to load the necessary CSS and javascript bits and to include a `$(document).ready()` function that initializes the DataTables library.

You can change the options used to initialize the DataTables library by editing the call to `$('table.display').DataTable()` in the default layout.  The available options for Datatables are described in the [DataTable documentation](https://www.datatables.net/manual/options), which is excellent.

You also must add a class of `display` to your tables.  You can change the class, but then you'll need to change the trigger defined in the `$(document).ready()` function in the default layout from `table.display` to the class you prefer.

You can also add page-specific triggers (by copying the `<script></script>` block from the default layout into the page) and classes, which lets you use different options on different tables.

If you use an HTML table, adding `class="display"` to the `<table>` tag is sufficient.

Markdown, however, doesn't allow you to add classes to tables, so you'll need to use a trick: add `<div class="datatable-begin"></div>` before the table and `<div class="datatable-end"></div>` after the table.  The default layout includes a jQuery snippet that automagically adds the `display` class to any table it finds between those two markers.  So you can start with this (we've trimmed the descriptions for display):

```markdown
<div class="datatable-begin"></div>

Food    | Description                           | Category | Sample type
------- | ------------------------------------- | -------- | -----------
Apples  | A small, somewhat round ...           | Fruit    | Fuji
Bananas | A long and curved, often-yellow ...   | Fruit    | Snow
Kiwis   | A small, hairy-skinned sweet ...      | Fruit    | Golden
Oranges | A spherical, orange-colored sweet ... | Fruit    | Navel

<div class="datatable-end"></div>
```

and get this:

<div class="datatable-begin"></div>

Food    | Description                                                                                       | Category | Sample type
------- | ------------------------------------------------------------------------------------------------- | -------- | -----------
Apples  | A small, somewhat round and often red-colored, crispy fruit grown on trees.                       | Fruit    | Fuji
Bananas | A long and curved, often-yellow, sweet and soft fruit that grows in bunches in tropical climates. | Fruit    | Snow
Kiwis   | A small, hairy-skinned sweet fruit with green-colored insides and seeds.                          | Fruit    | Golden
Oranges | A spherical, orange-colored sweet fruit commonly grown in Florida and California.                 | Fruit    | Navel

<div class="datatable-end"></div>


Notice a few features:

* You can keyword search the table. When you type a word, the table filters to match your word.
* You can sort the column order.
* You can page the results so that you show only a certain number of values on the first page and then require users to click next to see more entries.

Read more of the [DataTable documentation](https://www.datatables.net/manual/options) to get a sense of the options you can configure. You should probably only use DataTables when you have long, massive tables full of information.

{% include note.html content=" Try to keep the columns to 3 or 4 columns only. If you add 5+ columns, your table may create horizontal scrolling with the theme. Additionally, keep the column heading titles short." %}

{% include links.html %}
