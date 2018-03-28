import java.util.*;
import java.io.*;
import java.lang.*;

/*
	Gabriel Pridham
	N01383793
	
	System Software
	COP 3404
	
	Project3 
	Pass One of Assembler

	TODO
	- Implement LTORG - Done
	- Fix the results of errors when dealing with the hashing
	- Program is case sensitive... assumes the file content will have all capital letters
	- Create Error messages for invalid mneumonics and duplicate or undefined labels
	- Create more test files
	- BYTE
	- Prob ignore label if mneumonic isnt valid
	- Change the hash addressing to hash the addresses first and then have seperate method to display DONE
	- ERROR GENERATE if the constant is hex and is not even
	- Fix error messages to print properly
	- Doesnt check to make sure that the mnuemonics that need operands have the operands
*/

public class Proj3
{
	public static void main( String[] args) throws FileNotFoundException
	{
		File file = new File(args[0]);
		PassOne passOne = new PassOne( file );
		
		passOne.printFile();
		System.out.println("");
		passOne.displayHashInfo();
	}
}

class PassOne
{
	private Addressing m_addressing;
	private ArrayList<StringInfo> m_content;
	
	PassOne(File file) throws FileNotFoundException
	{
		Scanner input = new Scanner( file ); // reads file name from console
		
		// parses file to get information
		m_content = readFile(file);
		
		m_addressing = new Addressing(m_content); // gets the addresses and hashes addresses
		
		checkOperands(); // checks the labels used in operands are defined
		
	}
	
	public ArrayList<StringInfo> readFile(File file) throws FileNotFoundException
	{
		Scanner input = new Scanner( file );
		ArrayList<StringInfo> content = new ArrayList<StringInfo>();
		String currentLine;
		
		while(input.hasNext() )
		{
			currentLine = input.nextLine();
			StringInfo line = new StringInfo(currentLine);
			
			content.add(line); //stores current line in array
		}
		
		
		return content;
	}
	
	public void printFile()
	{
		// prints file with addresses
		for(int index = 0; index < m_content.size(); index++ )
		{
			System.out.println(m_content.get(index).toString());
			
			m_content.get(index).Errors(); // prints the errors that this line might contain
		}
	}
	
	public void displayHashInfo()
	{
		m_addressing.getHashTable().displayTable();; // prints the hash table information
	}
	
	public void checkOperands()
	{
		for(int index = 0; index < m_content.size(); index++)
		{
			String operand = m_content.get( index ).getOperand();
			//if(operand != null)
			//{
				boolean regToReg = m_content.get( index ).isRegToReg();
				
				OperandInfo ope = new OperandInfo(operand, regToReg );
				HashTable addrTable = m_addressing.getHashTable();
				
				if(ope.containsLabel())
				{
					//System.out.println("Hashing: " + ope.getLabel());
					if( addrTable.find( ope.getLabel() ) == null )
					{
						//System.out.println("Error " + ope.getLabel() + " is undefined" );
						m_content.get(index).setError(1);// sets error for undefined label
					}
				}
				
				if(!ope.validRegisters())
					m_content.get( index ).setError(5); // error for invalid registers
			//}
		}
	}
	
	public ArrayList<StringInfo> getContent() { return m_content; }
	
	
} // end of passone

class MyString
{
	private char[] string;
	
	MyString(String string)
	{
		this.string = new char[80];
		this.string = string.toCharArray();
		
	}
	
	/*
	 ************ substring ************
	 * Written specifically for the String layer specifications
	 *
	 *
	 ******************************
	*/
	public String substring(int start, int end)
	{
		int length = end - start + 1 ;
		String substring = new String();
		int subIndex = 0;
		int index = start;
		
		//System.out.println("String " + new String( string) );
		//System.out.println("Start: " + start + " End: " + end + " string length " + string.length);
		//System.out.println("Content: " + new String( string ) + "Length: " + string.length);
		//System.out.println("Sub: " + string[index]);
		
		// copies contents if start has a character or if its the whole comment section
		while( index < string.length  && index <= end   )
		{
			if(Character.isWhitespace(string[index]) && start != 31)
				break;
			substring += string[index];
			//substring[subIndex] = string[index];
			index++;
			//subIndex++;
			
		}
		
		//System.out.println( "Substring: " + new String( substring ) );
		
		return substring;
	} // end of substring
	
	/*
	 ************ charAr ************
	 *
	 * Returns null character if location is bigger than array size
	 *
	 ******************************
	*/
	public char charAt(int location)
	{
		if(location >= string.length)
			return '\0';
		else
			return string[location];
	} // end of charAt
	
	/*
	 ************ toStringType *******
	 *
	 *
	 *
	 ******************************
	*/
	public String toStringType()
	{
		return new String( string );
	} // end of charAt
	
	public int length()
	{
		return string.length;
	}
}

/*
 **** StringInfo ****
 
 - deals with figuring out the contents of string
*/

class StringInfo
{
	private MyString content;
	private String label; // optional
	private String mneumonic;
	private String operand; // label, register, ',' , x optional
	private String comment;
	private int address;
	private int byteSize;
	private int constantSize = 0; // LTORG
	private DataLink symbol; // contains label and associated location
	
	private ArrayList<Integer> errors = new ArrayList<Integer>();
	
	//private boolean storage = false; // WORD, RESW, RESB
	//private boolean start = false;
	//private boolean end = false;
	//private boolean base = false;
	private boolean word = false;
	private boolean resw = false;
	private boolean resb = false;
	private boolean presentLabel = false;
	private boolean lineComment = false; // true if there comment initialized with a period and not in its comment column
	private boolean colComment = false; // true if comment in comment line
	private boolean extended = false;
	private boolean immediate = false; // immediate addressing
	private boolean indirect = false; // indirect addressing
	private boolean ltorg = false; 
	private boolean invalidMneumonic = false;
	private boolean regToReg = false; // determines if Using a mneumonic that is register to register
	
	StringInfo(String content)
	{
		this.content = new MyString(content);
		parseString();
		stringContents(); // set boolean values for content
		checkMneumonic();
		generateBytes();
		
		//OperandInfo ope = new OperandInfo(operand, regToReg);
		
		//System.out.println("Line: " + content);
	}
	
	private void parseString()
	{
			if( content.charAt(0) == '.' ) // comment line
			{
				comment = content.toStringType();
				lineComment = true;
				
			}
			else
			{
				label = content.substring(0,7);
				
				mneumonic = content.substring(10,16);
				
				operand = content.substring(19, 28);
				
				comment = content.substring(31, 79);
				
			
			}
	}
	/*
	 ************ stringContents *******
	 *   Sets the boolean values of line
	 *
	 *  
	 ******************************
	*/
	private void stringContents()
	{

		if( !lineComment && !label.isEmpty() ) // may need more testing.. but seems to work fine
			presentLabel = true;
	
		if(content.charAt(9) == '+')
		{
			extended = true;
			
		}
		word = content.toStringType().contains("WORD");
		resw = content.toStringType().contains("RESW");
		resb = content.toStringType().contains("RESB");
		
		checkSymbol(content.charAt(18));
		
		
		
	} // end stringContents
	
	private void generateBytes()
	{
		Mneumonic[] mneu = Mneumonics.getMneumonics();
		
		if( Mneumonics.find(mneumonic) != -1 )
		{
			byteSize = Mneumonics.getSize(mneumonic);
			
			if(byteSize == 2)
				regToReg = true; // using a Register to Register Mneumonic
			
			if(extended)
				byteSize++;
			//System.out.println(mneumonic + " " +byteSize);
		}
		else if( word )
			byteSize = 3;
		else if(  resw )
			byteSize = Integer.parseInt( operand ) * 3;
		else if( resb )
			byteSize = Integer.parseInt( operand );
		else
			byteSize = 0; // may need to remove.. hasnt been tested
		
	}
	
	/*
		Checks if the mneumonic is an assembler directive or a valid instruction
	*/
	public void checkMneumonic()
	{
		invalidMneumonic = true;
		String[] directives = { new String("START"),
									  new String("END"),
									  new String("BASE"),
									  new String("LTORG"),
									  new String("WORD"),
									  new String("RESW"),
									  new String("RESB")};

		if(!lineComment && content.length() != 0) // mneumonic cant be invalid if line is comment or empty
		{
			if(Mneumonics.find(mneumonic) != -1)
			{
				invalidMneumonic = false;
			}
			else 
			{
				for(int index = 0; index < directives.length; index++)
				{
					if( mneumonic.equals(directives[index] ))
						invalidMneumonic = false;
				}
			}
			
			if(invalidMneumonic)
				setError(4); // sets error flag for invalid Mneumonic
		}
		
	}
	/*
	 ************ checkSymbol *******
	 *   
	 *
	 *
	 ******************************
	*/
	private void checkSymbol(char symbol)
	{
		switch(symbol)
		{
			case '#': 	immediate = true;
							break;
			case '@':	indirect = true;
							break;
			case '=':    ltorg = true;
							break;
			
		}
	} // end checkSymbol
	
	
	private void checkColComment()
	{
		for( int index = 0; index < comment.length(); index++)
		{
			if(comment.charAt(index) != ' ' )
			{
				colComment = true;
				break;
			}
		}
	} // end checkColComment
	
	/*
	 * toString
	*/
	public String toString()
	{
		String string;
		String addr = Integer.toHexString( address ).toUpperCase();
		
		
		
		if(lineComment)
			string= comment;
		else
			string = String.format("%-4s %s", addr, content.toStringType().toString());
			//string =   String.format("%-4s %-8s %-8s %-9s %s ", addr, label, mneumonic, operand, comment );
			//string = address + " " + label + " " + mneumonic + " " + operand + " " + comment;
		
		return string;
	
	} // end toString
	
	public void Errors()
	{
		if(!lineComment)
		{
			for(int index = 0; index < errors.size(); index++)
			{
				
			
				switch(errors.get(index))
				{
					case 1:	System.out.println("****Error " + operand + " is undefined" ); // undefined label
								break;
					case 2:	System.out.println("****Error Hex constants need to be even length.");
								break;
					
					case 3: System.out.println(String.format("***Error  %s already exists.",  label));// Label already exist with a value
								break;
					case 4: System.out.println("****Error " + mneumonic + " is an invalid Mneumonic."); //Invalid Mneumonic
								break;
					
					case 5: System.out.println("****Error " + operand + " contains invalid register");// Invalid Register
								break;
				}
			}
		}
	}
	
	public void setError(int num)
	{
		errors.add( num );
	}
	
	public void setSymbol(DataLink sym) { symbol = sym; }
	public DataLink getSymbol(){ return symbol; }
	public void setAddress(int addr){ this.address = addr; }
	public int getAddress() { return address; }
	public boolean extended() { return extended; }
	public String getMneumonic() { return mneumonic; }
	public String getOperand() {	return operand; }
	public int getBytes(){ return byteSize; }
	public boolean hasLabel() { return presentLabel; }
	public boolean isCommentLine() { return lineComment; }
	public String getLabel() {	return label;}
	public boolean isLtorg() { return ltorg; }
	public boolean isInvalidMneumonic() { return invalidMneumonic; }
	public boolean isRegToReg(){ return regToReg; }
	
	
	// ERROR GENERATE if the constant is hex and is not even
	public int getConstantSize()
	{
		if(!ltorg) // just in case method is called when it shouldn't be called
			return 0;
		int size = 0;
		char constantToken = operand.charAt(0);
		String constantValue = operand.substring(2, operand.length() - 1);
		
		//System.out.println("Constant String: " + constantValue); // for debugging
		
		switch (constantToken)
		{
			case 'X':	if( constantValue.length() % 2 != 0) 
							{
								setError(2);// sets error for uneven Hex constant
								//System.out.println("ERROR.. Hex constants need to be even length. This Constant will be ignored.");
								size = 0;
							}
							else
								size = constantValue.length() / 2;
							break;
							
			case 'C':	size = constantValue.length();
							break;
		}
		
		return size;
	}
	
} // end StringInfo

/********* OperandInfo ***********
 *	Col 20-29  (operand) label, register, ',',X optional  ...
 * Deals with all operands except the literals.. StringInfo Deals with the literals
*/
class OperandInfo
{
	private String m_operand;
	
	//private int m_registers = 0;
	
	private boolean m_isLabel = false;
	private boolean m_useRegister = false;
	private boolean m_isIndexed = false;
	private boolean m_isInteger = false;
	private boolean m_validRegisters = true;
	//private boolean m_isLiteral = false;
	
	private String m_label;
	
	
	OperandInfo(String operand, boolean registers)
	{
		//System.out.print("\nOperand: " + operand);
		if(operand != null)
		{
			
			if(registers) // Register to Register
			{
				if(operand.contains(",")) // using two registers
				{
					String leftReg = operand.substring(0, operand.indexOf(",") ); // left side of comma
					String rightReg = operand.substring(operand.indexOf(",") + 1); // right size of comma
					
					m_validRegisters = (checkRegister(leftReg) && checkRegister(rightReg));
					
				}
				
			}
			else // not using register to register mneumonic
			{
				if(operand.matches("\\d+")) // operand contains numbers
					m_isInteger = true;
				else if(operand.matches("[A-Z]+"))
				{
					m_isLabel = true;
					m_label = operand;
				}
				
				else if(operand.contains(",")) // using Index register
				{
					String label = operand.substring(0, operand.indexOf(",") ); // Label
					String index = operand.substring(operand.indexOf(",") + 1); // Index reg
					if(operand.charAt(operand.length() - 1) == 'X')
					{
						m_isLabel = true;
						m_isIndexed = true;
						
						m_label = label;
					}
				} // end else ... reg to reg
				
			
			}
		}
			
	}
	/*
		Can use this Method to get the Register value for Pass Two
	*/
	public boolean checkRegister(String register)
	{
		
									
		char[] registers = {'A', 'X', 'L', 'B', 'S', 'T', 'F'};
		
		if(register.length() == 1)
		{
			for(int index = 0; index < registers.length; index++)
			{
				if( registers[index] == register.charAt(0) )
					return true;	
			}
		}
		else
		{
			if(register.equals("PC") || register.equals("SW"))
				return true;
		}
		
		/*
		switch( register)
		{
			case "A":	//System.out.println("Register A");
							break;
			case "X": 	//System.out.println("Register X");
							break;
			case "L":	//System.out.println("Register L");
							break;
			case "PC": 	//System.out.println("Register PC");
							break;
			case "SW":	//System.out.println("Register SW");
							break;
			case "B":	//System.out.println("Register B");
							break;
			case "S":	//System.out.println("Register S");
							break;
			case "T":	//System.out.println("Register T");
							break;
			case "F":	//System.out.println("Register F");
							break;
			default:		//System.out.println("Error, invalid Register");
							break;
		}
		*/
		
		return false;
	}
	
	public boolean containsLabel(){ return m_isLabel; };
	public String getLabel() { return m_label; }
	public boolean validRegisters(){ return m_validRegisters; }
	
	
}

class Addressing
{
	private int pc = 0; // program counter
	private int base; // base address
	private int ltorgSize = 0; // current size of ltorg memory before LTORG 
	
	private HashTable hashTable;
	private ArrayList<StringInfo> content; // the whole file
	private ArrayList<DataLink> labels = new ArrayList<DataLink>(); // array of label content
	
	
	Addressing(ArrayList<StringInfo> content)
	{
		this.content = content;
		
		
		generateAddresses();
		//hashAddresses();
		//displayLabels();
	}
		/*
		** TEMPORARY METHOD - displays the contents that will be hashed PROB DELETING
			DOnt even use the labels Arraylist anymore
		*/
		/*
	public void displayLabels()
	{
		for( int index = 0; index < labels.size(); index++ )
		{
			int addr = labels.get( index ).getValue();
			System.out.println( labels.get( index ).getKey() + " " + Integer.toHexString( addr ).toUpperCase());
		}
	}
	*/
	
	/*
		PASS ONE
		GenerateAddresses essentially does pass one
	*/ 
	private void generateAddresses()
	{
		hashTable = new HashTable( 3 * content.size());
		StringInfo currentLine;
		for(int index = 0; index < content.size(); index++ )
		{
			currentLine = content.get( index );
			
			//System.out.println(currentLine);
			// NEED TO CHANGE
			if( currentLine.getMneumonic() != null && currentLine.getMneumonic().contains("START") )
			{
				pc = Integer.parseInt( currentLine.getOperand(), 16 ); // grabs hex string and converts value to decimal 
			}
			/*
				LTORG IMPLEMENTATIONS
			*/
			if( currentLine.isLtorg() && !currentLine.isInvalidMneumonic())
			{
				ltorgSize += currentLine.getConstantSize();
			}
			
			currentLine.setAddress(pc); // this needs to be before the updated PC
			
				// HASH label and location
			if( currentLine.hasLabel() )
			{
				
				DataLink link = new DataLink(currentLine.getLabel(), currentLine.getAddress() );
				int location = hashTable.hashFunc(link.getKey());
				
				if( link.getValue() != -1  ) // if attempting to insert new data
				{
					if ( hashTable.find( link.getKey() )!= null ) // if 
					{
						currentLine.setError(3); // sets error for duplicate labels
						//System.out.println(String.format("ERROR  %s already exists at location %d with value %X",  link.getKey(), location, hashTable.find( link.getKey() ).getValue()));
					}
					else
						hashTable.insert( link );
				}
				
				
			}
			
			if(!currentLine.isCommentLine() && currentLine.getMneumonic().equals( "LTORG") )
			{
				pc += ltorgSize;
				ltorgSize = 0; 
			}
			
			if( !currentLine.isCommentLine()  )
			{
				pc += currentLine.getBytes();
				//System.out.println(Integer.toHexString( pc ).toUpperCase());
			}
	
		}
		
		
	}
	
	/* // not using
	public void hashAddresses()
	{
	    hashTable = new HashTable( 3 * labels.size());
		DataLink link;
		StringInfo currentLine;
		
		for(int index = 0; index < content.size(); index++)
		{
			currentLine = content.get( index );
			if( currentLine.hasLabel())
			{
				link = currentLine.getSymbol();
				int location = hashTable.hashFunc(link.getKey());
				
				
				if( link.getValue() != -1  ) // if attempting to insert new data
				{
					if ( hashTable.find( link.getKey() )!= null ) // if 
					{
						currentLine.setError(3); // sets error for duplicate labels
						//System.out.println(String.format("ERROR  %s already exists at location %d with value %X",  link.getKey(), location, hashTable.find( link.getKey() ).getValue()));
						
					}
					else
					{
						hashTable.insert(labels.get(index));
						if( hashTable.collision( location ) )
						{
							// do something if collision needs to be detected
						}
							
						//System.out.println( String.format("%-14d %-8s %-7s main  main", location, link.getKey(),  Integer.toHexString(link.getValue()).toUpperCase()));
						
					}
				}
					
				else // if trying to search for data
				{
					if( hashTable.find( link.getKey() ) != null ) // found data inof
						System.out.println( link.getKey() + " stored at location " + location + " with value: " + hashTable.find( link.getKey()).getValue());
					else  // if no data exsist
						System.out.println("ERROR " + link.getKey() + " not found" );
				}
					
	
			}
		}
	
		
	} // end of hashAddresses

	*/
	
	public HashTable getHashTable(){ return hashTable; }
	
	
	
	/*
	 ****** nextAddress ******
		int bytes: Bytes that are taken up
		
		Updates program counter by incrementing by the bytes currently entered
	 
	*/
	public int nextAddress(int bytes)
	{
		pc += bytes;
		
		return pc;
	}
	
	
	
}
