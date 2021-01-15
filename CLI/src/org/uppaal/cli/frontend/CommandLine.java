package org.uppaal.cli.frontend;

import org.jline.reader.CompletingParsedLine;
import org.jline.reader.ParsedLine;
import java.util.function.Predicate;
import java.util.Collections;
import java.util.Objects;
import java.util.LinkedList;
import java.util.List;

/**
* class describing a command line, store the information for completion and execution
*/

public class CommandLine implements ParsedLine, CompletingParsedLine
	{
		private char[] escapeChars = {'\\'};

	private char[] quoteChars = {'\'', '"'};

// the command line being edited
		private String line;

// the list of words
		private LinkedList<String> words;

// wordIndex the index of the current word in the list of words
		private int wordIndex;

// the cursor position within the current word
		private int wordCursor;

// the cursor position within the line
		private int cursor;


//the opening quote (usually '\"' or '\'') or null
		private String openingQuote;

// the cursor position inside the raw word (i.e. including quotes and escape characters)
		private int rawWordCursor;

// the raw word length, including quotes and escape characters
		private int rawWordLength;

		public CommandLine() {
			this.line = null;
			this.words = new LinkedList<String>();
			this.wordIndex = 0;
			this.wordCursor = 0;
			this.cursor = 0;
			this.openingQuote = null;
			this.rawWordCursor = 0;
			this.rawWordLength = 0;
		}

/**
* reset this command line
* @param line the new inspected line
*/
public void reset (String line) {
			this.line = line;
			this.words.clear();
			this.wordIndex = 0;
			this.wordCursor = 0;
			this.cursor = 0;
			this.openingQuote = null;
			this.rawWordCursor = 0;
			this.rawWordLength = 0;
}

public int wordIndex() {
	return this.wordIndex;
}

/**
* set the word index of this command line
* @param index the new word index of this command line
*/
public void setWordIndex(int index) {
	this.wordIndex = index;
}

		public String word() {
			// TODO: word() should always be contained in words()
			if ((wordIndex < 0) || (wordIndex >= words.size())) {
				return "";
			}
			return words.get(wordIndex);
		}

/**
* add a new word to the list of words
* @param word the new word to add
*/
public void addWord(String word) {
	if (word==null) word ="";
	this.words.addLast(word);
	this.wordIndex = this.words.size()-1;
	this.wordCursor = word.length();
	this.rawWordLength = word.length();
	this.rawWordCursor = this.rawWordLength;
}

public int wordCursor() {
	return this.wordCursor;
}

/**
* set the word cursor of this command line
* @param cursor the new word cursor for this command line
*/
public void setWordCursor(int cursor) {
	this.wordCursor = cursor;
}

public List<String> words() {
	return this.words;
}

public int cursor() {
	return this.cursor;
}

/**
* set the cursor of this command line
* @param cursor the new cursor for this command line
*/
public void setCursor(int cursor) {
	this.cursor = cursor;
}

		public String line() {
			return line;
		}

		public CharSequence escape(CharSequence candidate, boolean complete) {
			StringBuilder sb = new StringBuilder(candidate);
			Predicate<Integer> needToBeEscaped;
			String quote = openingQuote;
			boolean middleQuotes = false;
			if (openingQuote==null) {
				for (int i=0; i < sb.length(); i++) {
					if (isQuoteChar(sb, i)) {
						middleQuotes = true;
						break;
					}
				}
			}
			if (escapeChars != null) {
				// Completion is protected by an opening quote:
				// Delimiters (spaces) don't need to be escaped, nor do other quotes, but everything else does.
				// Also, close the quote at the end
				if (openingQuote != null) {
					needToBeEscaped = i -> isRawEscapeChar(sb.charAt(i)) || String.valueOf(sb.charAt(i)).equals(openingQuote);
				}
				// Completion is protected by middle quotes:
				// Delimiters (spaces) don't need to be escaped, nor do quotes, but everything else does.
				else if (middleQuotes) {
					needToBeEscaped = i -> isRawEscapeChar(sb.charAt(i));
				}
				// No quote protection, need to escape everything: delimiter chars (spaces), quote chars
				// and escapes themselves
				else {
					needToBeEscaped = i -> isDelimiterChar(sb, i) || isRawEscapeChar(sb.charAt(i)) || isRawQuoteChar(sb.charAt(i));
				}
				for (int i = 0; i < sb.length(); i++) {
					if (needToBeEscaped.test(i)) {
						sb.insert(i++, escapeChars[0]);
					}
				}
			} else if (openingQuote == null && !middleQuotes) {
				for (int i = 0; i < sb.length(); i++) {
					if (isDelimiterChar(sb, i)) {
						quote = "'";
						break;
					}
				}
			}
			if (quote != null) {
				sb.insert(0, quote);
				if (complete) {
					sb.append(quote);
				}
			}
			return sb;
		}

@Override
public int rawWordCursor() {
	return rawWordCursor;
}

/**
* set the raw word cursor of this command line
* @param cursor the new raw word cursor of this command line
*/
public void setRawWordCursor(int cursor) {
	this.rawWordCursor = cursor;
}

		@Override
public int rawWordLength() {
	return rawWordLength;
}

/**
* set the raw word length of this command line
* @param length the new raw word length of this command line
*/
public void setRawWordLength(int length) {
	this.rawWordLength = length;
}

/**
* check if a character is an escape character
* @param ch the character to check
* @return true if and only if ch is an escape character
*/
public boolean isEscapeChar(char ch) {
		if (escapeChars != null) {
			for (char e : escapeChars) {
				if (e == ch) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if this character is a valid escape char (i.e. one that has not been escaped)
	 *
	 * @param buffer
	 *		  the buffer to check in
	 * @param pos
	 *		  the position of the character to check
	 * @return true if the character at the specified position in the given buffer is an escape
	 *		 character and the character immediately preceding it is not an escape character.
	 */
	public boolean isEscapeChar(final CharSequence buffer, final int pos) {
		if (pos < 0) {
			return false;
		}
		char ch = buffer.charAt(pos);
		return isEscapeChar(ch) && !isEscaped(buffer, pos);
	}

	/**
	 * Check if a character is escaped (i.e. if the previous character is an escape)
	 *
	 * @param buffer
	 *		  the buffer to check in
	 * @param pos
	 *		  the position of the character to check
	 * @return true if the character at the specified position in the given buffer is an escape
	 *		 character and the character immediately preceding it is an escape character.
	 */
	public boolean isEscaped(final CharSequence buffer, final int pos) {
		if (pos <= 0) {
			return false;
		}
		return isEscapeChar(buffer, pos - 1);
	}

	/**
	 * Returns true if the character at the specified position if a delimiter. 
	 *
	 * @param buffer
	 *		  the buffer to check in
	 * @param pos
	 *		  the position of the character to check
	 * @return true if the character at the specified position in the given buffer is a delimiter.
	 */
	public boolean isDelimiterChar(CharSequence buffer, int pos) {
		return Character.isWhitespace(buffer.charAt(pos));
	}

	private boolean isRawEscapeChar(char key) {
		if (escapeChars != null) {
			for (char e : escapeChars) {
				if (e == key) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRawQuoteChar(char key) {
		if (quoteChars != null) {
			for (char e : quoteChars) {
				if (e == key) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isQuoteChar(final CharSequence buffer, final int pos) {
		if (pos < 0) {
			return false;
		}
		if (quoteChars != null) {
			for (char e : quoteChars) {
				if (e == buffer.charAt(pos)) {
					return !isEscaped(buffer, pos);
				}
			}
		}
		return false;
	}
	}
