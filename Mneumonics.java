import java.util.*;



enum Register
{
	
	A(0),
	X(1),
	L(2),
	PC(8),
	SW(9),
	B(3),
	S(4),
	T(5),
	F(6);
	
	private final int num;
	
	Register(int num)
	{
		this.num = num;
	}
	
	public int num()
	{
		return num;
	}
	
}

class Mneumonic
{
	private String label;
	private int opCode; // stored as hex decimal
	private int f1; // format option 1 the format that will be used
	private int f2; // format option 2 NOT sure what this is used for 
	
	private int usedFormat;
	
	Mneumonic(String label, int opCode, int f1, int f2 )
	{
		this.label = label;
		this.opCode = opCode;
		this.f1 = f1;
		this.f2 = f2;
		
		
	}
	
	public int opCode()
	{
		return opCode;
	}
	
	public String toString()
	{
		return label + " " + opCode + " " + f1 + " " + f2;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public int getFormat()
	{
		return f1;
	}
}

public class Mneumonics
{
	
	public static Mneumonic[] getMneumonics()
	{
		return mneu;
	}
	
	/*
		Returns format if mneumonic found or 0 if not
	*/
	public static int getSize(String str)
	{
		int location = find(str);
		
		
		if(location >= 0)
			return mneu[location].getFormat();
		else 
			return 0;
	}
	
	/*
		returns location if found or -1 if not
	*/
	public static int find(String str)
	{
		for( int index = 0; index < mneu.length; index++)
		{
			if(mneu[index].getLabel().equals(str))
				return index;
		}
		
		return -1;
	}
	
	//public static void main(String args[])
	//{
	
		static Mneumonic[] mneu = {
		
		
		new Mneumonic( "+LDB" ,   0x68,    4,   3  ),
		new Mneumonic( "MULR" ,   0x98,    2,   0 ),
		new Mneumonic( "+SSK",    0xEC,    4,   0 ),
		new Mneumonic( "WD",      0xDC,    3,   1 ),
		new Mneumonic( "*STX",   0x10,    3,   0 ),
		new Mneumonic( "*OR",     0x44,    3,   3 ),
		new Mneumonic( "AND",     0x40,    3,   3 ),
		new Mneumonic( "*LDA",    0x00,    3,   3 ),
		new Mneumonic( "+JGT",    0x34,    4,   0 ),
		new Mneumonic( "+STL",    0x14,    4,   0 ),
		new Mneumonic( "*WD",     0xDC,    3,   1 ),
		new Mneumonic( "+STI",    0xD4,    4,   0 ),
		new Mneumonic( "LPS",      0xD0,    3,   0 ),
		new Mneumonic( "+LDT",    0x74,    4,   3 ),
		new Mneumonic( "*LDCH",  0x50,    3,   1 ),
		new Mneumonic( "*LDL",    0x08,    3,   3 ),
		new Mneumonic( "TIXR",    0xB8,    2,   0 ),
		new Mneumonic( "SUBF",   0x5C,    3,   0 ),
		new Mneumonic( "*JSUB",  0x48,    3,   0 ),
		new Mneumonic( "LDX",     0x04,    3,   3 ),
		new Mneumonic( "+MULF", 0x60,    4,   0 ),
		new Mneumonic( "+J",       0x3C,    4,   0 ),
		new Mneumonic( "SVC",     0xB0,    2,   0 ),
		new Mneumonic( "STT",     0x84,    3,   0 ),
		new Mneumonic( "+COMP", 0x28,    4,   3 ),
		new Mneumonic( "TIX",      0x2C,    3,   0 ),
		new Mneumonic( "FLOAT",  0xC0,    1,   0 ),
		new Mneumonic( "LDT",     0x74,    3,   3 ),
		new Mneumonic( "STA",     0x0C,    3,   0 ),
		new Mneumonic( "*TD",     0xE0,    3,   1 ),
		new Mneumonic( "SHIFTR", 0xA8,    2,   0 ),
		new Mneumonic( "STB",     0x78,    3,   0 ),
		new Mneumonic( "SIO",     0xF0,    1,   0 ),
		new Mneumonic( "LDA",     0x00,    3,   3 ),
		new Mneumonic( "HIO",     0xF4,    1,   0 ),
		new Mneumonic( "+STS",   0x7C,    4,   0 ),
		new Mneumonic("DIVF",     0x64,    3,   0 ),
		new Mneumonic( "*TIX",    0x2C,    3,   0 ),
		new Mneumonic( "+JSUB",  0x48,    4,   0 ),
		new Mneumonic( "LDCH",    0x50,    3,   1 ),
		new Mneumonic( "+COMPF", 0x88,    4,   0 ),
		new Mneumonic( "JEQ",       0x30,    3,   0 ),
		new Mneumonic( "*DIV",     0x24,    3,   3 ),
		new Mneumonic( "+STT",    0x84,    4,   0 ),
		new Mneumonic( "+SUBF",   0x5C,    4,   0 ),
		new Mneumonic( "*AND",    0x40,    3,   3 ),
		new Mneumonic( "+OR",      0x44,    4,   3 ),
		new Mneumonic( "SSK",      0xEC,    3,   0 ),
		new Mneumonic( "+JLT",     0x38,    4,   0 ),
		new Mneumonic( "*RD",      0xD8,    3,   1 ),
		new Mneumonic( "LDS",      0x6C,    3,   3 ),
		new Mneumonic( "*MUL",    0x20,    3,   3 ),
		new Mneumonic( "+LDS",    0x6C,    4,   3 ),
		new Mneumonic( "+DIV",    0x24,    4,   3  ),  
		new Mneumonic( "J",         0x3C,   3,   0 ),
		new Mneumonic( "+MUL",   0x20,    4,   3 ),
		new Mneumonic( "*COMP", 0x28,    3,   3 ),
		new Mneumonic( "+STX",   0x10,    4,   0 ),
		new Mneumonic( "*J",       0x3C,    3,   0 ),
		new Mneumonic( "+LDA",   0x00,    4,   3 ),
		new Mneumonic( "+SUB",   0x1C,    4,   3 ),
		new Mneumonic( "+STB",   0x78,    4,   0 ),
		new Mneumonic( "*JLT",    0x38,    3,   0 ),
		new Mneumonic( "SUB",     0x1C,    3,   3 ),
		new Mneumonic( "+ADDF",  0x58,    4,   0 ),
		new Mneumonic( "RD",       0xD8,    3,   1 ),
		new Mneumonic( "*JEQ",    0x30,    3,   0 ),
		new Mneumonic( "LDB",      0x68,    3,   3 ),
		new Mneumonic( "RSUB",    0x4C,    3,   0 ),
		new Mneumonic( "MULF",    0x60,    3,   0 ),
		new Mneumonic( "JSUB",    0x48,    3,  0 ),
		new Mneumonic( "SUBR",    0x94,    2,   0 ),
		new Mneumonic( "DIVR",    0x9C,    2,   0 ),
		new Mneumonic( "LDL",      0x08,    3,   3 ),
		new Mneumonic( "+JEQ",    0x30,    4,   0 ),
		new Mneumonic( "+STCH",  0x54,    4,   0 ),
		new Mneumonic( "*STL",    0x14,    3,   0 ),
		new Mneumonic( "+STA",    0x0C,    4,   0 ),
		new Mneumonic( "STSW",   0xE8,    3,   0 ),
		new Mneumonic( "COMPF",  0x88,    3,   0 ),
		new Mneumonic( "+DIVF",   0x64,    4,   0 ),
		new Mneumonic( "+STF",    0x80,    4,   0 ),
		new Mneumonic( "TIO",      0xF8,    1,   0 ),
		new Mneumonic( "*ADD",    0x18,    3,   3 ),
		new Mneumonic( "*STSW",  0xE8,    3,   0 ),
		new Mneumonic( "+STSW",  0xE8,    4,   0 ),
		new Mneumonic( "+LPS",     0xD0,    4,   0 ),
		new Mneumonic( "JLT",       0x38,    3,  0 ),
		new Mneumonic( "*JGT",    0x34,    3,   0 ),
		new Mneumonic( "MUL",     0x20,    3,  3 ),
		new Mneumonic( "+LDL",    0x08,    4,   3 ),
		new Mneumonic( "OR",       0x44,    3,   3 ),
		new Mneumonic( "COMP",   0x28,    3,   3 ),
		new Mneumonic( "TD",       0xE0,    3,   1 ),
		new Mneumonic( "STS",     0x7C,    3,   0 ),
		new Mneumonic( "*STCH",  0x54,    3,   0 ),
		new Mneumonic( "LDF",      0x70,    3,   0 ),
		new Mneumonic( "ADD",     0x18,    3,   3 ),
		new Mneumonic( "FIX",      0xC4,    1,   0 ),
		new Mneumonic( "*RSUB",  0x4C,    3,   0 ),
		new Mneumonic( "NORM",   0xC8,    1,   0 ),
		new Mneumonic( "STF",     0x80,    3,   0 ),
		new Mneumonic( "*LDX",    0x04,    3,   3 ),
		new Mneumonic( "CLEAR",   0xB4,    2,   0 ),
		new Mneumonic( "+RSUB",   0x4C,    4,   0 ),
		new Mneumonic( "ADDF",    0x58,    3,   0 ),
		new Mneumonic( "+WD",     0xDC,    4,   1 ),
		new Mneumonic( "+LDCH",   0x50,    4,   1 ),
		new Mneumonic( "+LDF",     0x70,    4,   0 ),
		new Mneumonic( "+LDX",     0x04,    4,   3 ),
		new Mneumonic( "STCH",    0x54,    3,   0 ),
		new Mneumonic( "+ADD",    0x18,    4,   3 ),
		new Mneumonic( "+AND",    0x40,    4,  3 ),
		new Mneumonic( "*SUB",    0x1C,    3,   3 ),
		new Mneumonic( "STX",     0x10,    3,   0 ),
		new Mneumonic( "RMO",     0xAC,    2,   0 ),
		new Mneumonic( "COMPR",  0xA0,    2,   0 ),
		new Mneumonic( "SHIFTL",  0xA4,   2,  0 ),
		new Mneumonic( "STL",      0x14,    3,   0 ),
		new Mneumonic( "+TD",       0xE0,    4,   1 ),
		new Mneumonic( "ADDR",    0x90,    2,   0 ),
		new Mneumonic( "STI",       0xD4,    3,   0 ),
		new Mneumonic( "+TIX",     0x2C,   4,   0 ),
		new Mneumonic( "*STA",    0x0C,    3,   0 ),
		new Mneumonic( "JGT",      0x34,    3,   0 ),
		new Mneumonic( "DIV",      0x24,    3,   3 ),
		new Mneumonic( "+RD",     0xD8,    4,   1 ) }; 
	
	//} // end main
} // end menumonics class






