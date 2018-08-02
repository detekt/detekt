package cases

// reports 1 - line with just one space
 
// reports 1 - a comment with trailing space
// A comment 
// reports 1
class TrailingWhitespacePositive { 
	// reports 1 - line with just one tab
    
    // reports 1
	fun myFunction() { 
		// reports 1 - line with 1 trailing tab
		println("A message")	
	// reports 1
	}  
}
