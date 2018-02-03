
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class main_class {
	
	public static ArrayList<MOT> mot;
	public static int[] PT;
	public static int LC;
	public static java.util.HashMap<String,Integer> hm;
	public static String[][] ST, LT;
	public static MOT mobj,obj2;
	public static Boolean mnc_flag = false, inst_flag = false, lab_flag = false;
	protected static String IntMedCode;

	public static void main(String[] args) throws IOException{
		
		ArrayList<MOT> mot1 = Create_MOT();
		java.util.HashMap<String, Integer> hm1 = Create_MOTindex();
		
		PT = new int[10];
		LT = new String[20][2];
		ST = new String[20][2];
		IntMedCode = "";
		
		for(int i =0 ; i<20 ;i++)
			for(int j =0; j<2; j++)
				ST[i][j] = "";
		
		for(int i =0 ; i<20 ;i++)
			for(int j =0; j<2; j++)
				LT[i][j] = "";
		
		for(int i=0; i<10; i++)
			PT[i] = 0; 
			
		String ass_code_string = get_file_contents();
		
		String[] ass_code_string_line = ass_code_string.split("\\r?\\n");

		for(String ass_code_line : ass_code_string_line){
			
			pass1(ass_code_line, mot1, hm1);
			
		}
		
		System.out.println(LC);
		
		System.out.println(PT[2]);
		
		System.out.println(LT[0][0] + "" + LT[0][1]);
		System.out.println(LT[1][0] + " " + LT[1][1]);
		System.out.println(LT[2][0] + " " + LT[2][1]);
		System.out.println(LT[3][0] + " " + LT[3][1]);
		System.out.println(LT[4][0] + " " + LT[4][1]);
		System.out.println(ST[0][0] + " " + ST[0][1]);
		System.out.println(ST[1][0] + " " + ST[1][1]);
		System.out.println(ST[2][0] + " " + ST[2][1]);
		System.out.println(ST[3][0] + " " + ST[3][1]);
		
		System.out.println("\n\n" + IntMedCode);

	}
	
	private static void pass1(String ass_code_line, ArrayList<MOT> mot1, HashMap<String, Integer> hm1) {
		
		init();
		
		String chk_mnc = "(STOP|ADD|SUB|MULT|MOVER|MOVEM|COMP|BC|DIV|READ|PRINT|START|END|ORIGIN|EQU|LTORG|DS|DC)";
		String only_alpha = "[a-zA-Z]+";
		String unary = "(STOP|LTORG|END)";
		String mnc = "",cls = "";
		int m_code = 0,len = 0;

		String[] ass_code_space = ass_code_line.split("\\s+");
		
		if(ass_code_space[0].matches(chk_mnc)) {
			mobj = mot1.get(hm1.get(ass_code_space[0]));
			System.out.println(mobj.mnc+" "+mobj.cls+" "+mobj.m_code+" "+mobj.length+" ");
			mnc = mobj.mnc;
			cls = mobj.cls;
			m_code = mobj.m_code;
			len = mobj.length;
		}else if(ass_code_space[1].matches(chk_mnc)) {
			mobj = mot1.get(hm1.get(ass_code_space[1]));
			System.out.println(mobj.mnc+" "+mobj.cls+" "+mobj.m_code+" "+mobj.length+" ");
			mnc = mobj.mnc;
			cls = mobj.cls;
			m_code = mobj.m_code;
			len = mobj.length;
		}
		
		
		
		/*Checking the index 0 of the Line.
		 *Conditions as Below:
		 *1) Found the END statement.
		 *2) Found the mnemonic.
		 *3) Found the label.
		 *4) Wrong instruction.
		 ****Checking the index 1 of line.
		 *=> Conditions as below:
		 * 1) Mnemonic.
		 * 2) instruction.
		 * 3) Mnemonic not found at both index 0 and 1, Wrong Instruction.
		 * 4) STOP or LTORG found.
		 * 5) Wrong instruction.
		 ****Checking the index 2 of the line
		 *=> Conditions as below:
		 * */
		
		if(check_index_0(ass_code_space[0], chk_mnc, unary, only_alpha, mnc, cls, m_code, len)) {
			
			/*if(lab_flag) {
				IntMedCode = IntMedCode + " " + "S" + " " + "" + "\n";
			}
			return ;*/
			
		}
		
		else 
			if(check_index_1(ass_code_space, chk_mnc, mnc, cls, m_code, len)) {
				return ;
			}else
				if(check_index_2(ass_code_space, mnc, cls, m_code, len))
					return ;
				else {
					System.out.println("Error, unable to process complete instruction");
				}
		
		//mobj = mot1.get(hm1.get(ass_code_space[0]));
		//System.out.println(mobj.mnc+" "+mobj.cls+" "+mobj.m_code+" "+mobj.length+" "+ass_code_space[1]);
		
	}

	private static Boolean check_index_2(String[] ass_code_space, String mnc, String cls, int m_code, int len) {
		
		
		if(!ass_code_space[2].isEmpty() && inst_flag == false) {
			
			System.out.println(ass_code_space[2]+" Found instruction!");
			
			String eq = "[a-zA-Z]+";
			
			if(mnc.equals("EQU")) {
				
				for(int i=0; i<20; i++) {
					
					if(ST[i][0].equals(ass_code_space[2])) {
						
						for(int j=0; j<20; j++) {
							
							if(ST[j][0].equals(ass_code_space[0])) {
								
								ST[j][1] = ST[i][1];
								IntMedCode = IntMedCode + " S " + i;
								IntMedCode = IntMedCode + " S " + j + "\n";
								
							}
							
						}
						
					}
					
				}
				
				System.out.println("Done with this Instruction :)");
				LC++;
				return true;
				
			}
			
			if(cls.equals("IS")) {
				
				String[] ass_code_inst = ass_code_space[2].split(",");
				
				obj2 = mot.get(hm.get(ass_code_inst[0]));
				IntMedCode = IntMedCode + " " + obj2.cls + " " + obj2.m_code;
				
				if(!ass_code_inst[1].matches(eq)) {
					
					put_in_LT(ass_code_inst[1]);
					
				}else {
					
					put_in_ST(ass_code_inst[1],"post");
					
				}
				
			}else if(cls.equals("DL")) {
				
				if(mnc.equals("DS")) {
					
					for(int i=0; i<20; i++) {
						
						if(ST[i][0].equals(ass_code_space[0])) {
							IntMedCode = IntMedCode + " S " + i;
						}
						
					}
					IntMedCode = IntMedCode + " C " + ass_code_space[2] + "\n";
					
					update_ST(ass_code_space, "DS");
					
				}else if(mnc.equals("DC")) {
					
					for(int i=0; i<20; i++) {
						
						if(ST[i][0].equals(ass_code_space[0])) {
							IntMedCode = IntMedCode + " S " + i + "\n";
						}
						
					}
					IntMedCode = IntMedCode + " C " + ass_code_space[2] + "\n";
					
					update_ST(ass_code_space, "DC");
					
				}
				
			}
			
			System.out.println("Done with this Instruction :)");
			LC++;
			return true;
			
		}else if(ass_code_space[2].isEmpty() && inst_flag == false) {
			
			System.out.println("Wrong Instruction! \t no inst found!!");
			
		}

		return false;
		
	}

	private static Boolean check_index_1(String[] ass_code_space, String chk_mnc, String mnc, String cls, int m_code, int len) {
		
		if(ass_code_space[1].matches(chk_mnc) && (mnc_flag == false)) {
			
			System.out.println("Mnemonic found at index 1");
			IntMedCode = IntMedCode + " " + cls + " " + m_code;
			
		}else if(mnc_flag == false){
			
			System.out.println("No mnemonic found!, Wrong Instruction!!!");
			return true;
			
		}else if(!ass_code_space[1].matches(chk_mnc) && !ass_code_space[1].isEmpty()) {
			
			System.out.println(ass_code_space[1]+" Found instruction!");
			inst_flag = true;
			
			String eq = "[a-zA-Z]+";
			
			System.out.println(cls);
			
			if(cls.equals("IS")) {
				
				if(ass_code_space[0].equals("READ")) {
					System.out.println("Done with this Instruction :)");
					put_in_ST(ass_code_space[1],"post");
					LC++;
					return true;
				}
				
				if(ass_code_space[0].equals("PRINT")) {
					System.out.println("Done with this Instruction :)");
					LC++;
					return true;
				}
				
				String[] ass_code_inst = ass_code_space[1].split(",");
				
				obj2 = mot.get(hm.get(ass_code_inst[0]));
				IntMedCode = IntMedCode + " " + obj2.cls + " " + obj2.m_code;
				
				if(!ass_code_inst[1].matches(eq)) {
					
					put_in_LT(ass_code_inst[1]);
					
				}else {
					
					put_in_ST(ass_code_inst[1],"post");
					
				}
				
				
			}else if(cls.equals("AD")) {
				
				System.out.println(mnc);
				
				if(mnc.equals("START")) {
					LC = Integer.parseInt(ass_code_space[1]);
					IntMedCode = IntMedCode + " C " + ass_code_space[1]+"\n";
					System.out.println("Done with this Instruction :)");
					return true;
				}
				
				if(mnc.equals("ORIGIN")) {
					
					String[] ass_code_plus = ass_code_space[1].split("[0-9]+(?<=[-+*/()])|(?=[-+*/()])");
					
					System.out.println(ass_code_plus[0]);
					
					for(int i=0; i<20; i++) {
						
						if(ST[i][0].equals(ass_code_plus[0])) {
							
							LC = Integer.parseInt(ST[i][1]) + Integer.parseInt(ass_code_plus[1]);
							IntMedCode = IntMedCode + " C " + LC + "\n";
							
						}
						
					}
					
					System.out.println("Done with this Instructionmmm :)");
					return true;
				}
				
			}else if(cls.equals("DL")) {
				
				if(mnc.equals("DS")) {
					
					for(int i=0; i<20; i++) {
						
						if(ST[i][0].equals(ass_code_space[0])) {
							IntMedCode = IntMedCode + " S " + i + "\n";
						}
						
					}
					IntMedCode = IntMedCode + " C " + ass_code_space[2] + "\n";
					
					update_ST(ass_code_space, "DS");
					
					System.out.println("Done with this Instructionmmm :)");
					return true;
					
				}else if(mnc.equals("DC")) {
					
					for(int i=0; i<20; i++) {
						
						if(ST[i][0].equals(ass_code_space[0])) {
							IntMedCode = IntMedCode + " S " + i + "\n";
						}
						
					}
					IntMedCode = IntMedCode + " C " + ass_code_space[2] + "\n";
					
					update_ST(ass_code_space, "DC");
					
					System.out.println("Done with this Instruction :)");
					return true;
					
				}
				
			}
			
			System.out.println("Done with this Instruction MMM :)");
			LC++;
			return true;
			
		}else {
			
			System.out.println("Wrong Instruction !!");
			
		}
		
		return false;
		
	}

	private static void update_ST(String[] ass_code_space, String type) {
		
		if(type.equals("DS")) {
			
			for(int i=0; i<20; i++) {
				
				if(ST[i][0].equals(ass_code_space[0])) {
					
					ST[i][1] = Integer.toString(LC);
					LC = LC + Integer.parseInt(ass_code_space[2])-1;
					System.out.println(LC);
					
					
				}
				
			}
			
		}else {
			
			for(int i=0; i<20; i++) {
				
				if(ST[i][0].equals(ass_code_space[0])) {
					
					ST[i][1] = Integer.toString(LC);
					LC++;
					
				}
				
			}
			
		}
		
	}

	private static void put_in_LT(String string) {
		
		for(int i=0; i<20; i++) {
			
			if(LT[i][0].isEmpty()) {
				
				
				LT[i][0] = string;
				System.out.println(LT[i][0]);
				IntMedCode = IntMedCode + " L " + i + "\n";
				return ;
			}
		}
		
	}

	private static Boolean check_index_0(String ass_code_space, String chk_mnc, String unary, String only_alpha, String mnc, String cls, int m_code, int len) {
		
			if(ass_code_space.equals("END")) {
			
				System.out.println("END of the file");
				IntMedCode = IntMedCode + " " + cls + " " + m_code + "\n";
				update_LT();
				return true;
		
			}else if(ass_code_space.matches(chk_mnc)) {
		
				System.out.println("Mnemonic found at index 0");
				mnc_flag = true;
				
				
			
				if(ass_code_space.matches(unary)){
				
						if(ass_code_space.equals("LTORG")) {
					
								update_LT();
								System.out.println("Done with this Instruction :)");
								return true;
					
						}
						
						if(ass_code_space.equals("STOP")) {
							LC++;
							System.out.println("Done with this Instruction :)");
							IntMedCode = IntMedCode + " " + cls + " " + m_code + "\n";
							return true;
						}
							
						
					System.out.println("Done with this Instruction :)");
					LC++;
					return true;
				}
				
				IntMedCode = IntMedCode + " " + cls + " " + m_code;
				
			}else if(!ass_code_space.matches(chk_mnc) && ass_code_space.matches(only_alpha)) {
			
				System.out.println("Instruction has lable");
				put_in_ST(ass_code_space,"pre");
				lab_flag = true;
			
			}else {
			
				System.out.println("Wrong Instruction! \t No mnemonic found!");
			
			}
			
			return false;
		
	}

	private static void update_LT() {
		
		int pool = 0;
		
		for(int i=0; i<18; i++) {
			
			//System.out.println(LT[i][0]);
			
			if(LT[i][1].isEmpty() && !LT[i][0].isEmpty()) {
				
				
				LT[i][1] = Integer.toString(LC);
				LC++;
				pool++;
				char lit = LT[i][0].charAt(2);
				IntMedCode = IntMedCode + " AD 5 DL 2 c " + lit + "\n";
				
			}
			
		}
		
		update_PT(pool);
		
	}

	private static void update_PT(int pool) {
		
		for(int i=1; i<10; i++) {
			
			if(PT[i] == 0) {
				PT[i] = PT[i-1]+pool;
			}
			
		}
		
	}

	private static void init() {
		
		mnc_flag = false;
		inst_flag = false;
		lab_flag = false;
		
		mobj = new MOT();
		obj2 = new MOT();
		
	}

	private static void put_in_ST(String string, String status) {
		
		if(status.equals("pre")) {
			for (int i=0; i<20; i++) {
				
				if(ST[i][0].equals(string))
					return ;
				if(ST[i][0].isEmpty()) {
					
					ST[i][0] = string;
					ST[i][1] = Integer.toString(LC);
					//IntMedCode = IntMedCode + " S " + i + "\n";
					return ;
					
				}
				
			}
		}else if(status.equals("post")) {
			for (int i=0; i<20; i++) {
				
				if(ST[i][0].equals(string))
					return ;
				
				if(ST[i][0].isEmpty()) {
					
					ST[i][0] = string;
					IntMedCode = IntMedCode + " S " + i + "\n";
					return ;
					
				}
				
			}
		}
		
	}

	private static String get_file_contents() throws IOException {
		
		File file = new File("C:\\Users\\mangesh\\Documents\\JAVA\\ass_code.txt");
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);

		int f_count = fis.available();

		byte[] b = new byte[f_count];
		dis.readFully(b);

		String ass_code_string = new String(b);
		
		dis.close();
		fis.close();
		
		return ass_code_string;
	}

	public static ArrayList<MOT> Create_MOT(){

		mot = new ArrayList<MOT>();
		mot.add(new MOT("STOP", "IS", 00, 1));
		mot.add(new MOT("ADD", "IS", 01, 1));
		mot.add(new MOT("SUB", "IS", 02, 1));
		mot.add(new MOT("MULT", "IS", 03, 1));
		mot.add(new MOT("MOVER", "IS", 04, 1));
		mot.add(new MOT("MOVEM", "IS", 05, 1));
		mot.add(new MOT("COMP", "IS", 06, 1));
		mot.add(new MOT("BC", "IS", 07, 1));
		mot.add(new MOT("DIV", "IS", 8, 1));
		mot.add(new MOT("READ", "IS", 9, 1));
		mot.add(new MOT("PRINT", "IS", 10, 1));
		mot.add(new MOT("START", "AD", 01, 0));
		mot.add(new MOT("END", "AD", 02, 0));
		mot.add(new MOT("ORIGIN", "AD", 03, 0));
		mot.add(new MOT("EQU", "AD", 04, 0));
		mot.add(new MOT("LTORG", "AD", 05, 0));
		mot.add(new MOT("DS", "DL", 01, 0));
		mot.add(new MOT("DC", "DL", 02, 1));
		mot.add(new MOT("AREG", "RG", 01, 0));
		mot.add(new MOT("BREG", "RG", 02, 0));
		mot.add(new MOT("CREG", "RG", 03, 0));
		mot.add(new MOT("EQ", "CC", 01, 0));
		mot.add(new MOT("LT", "CC", 02, 0));
		mot.add(new MOT("GT", "CC", 03, 0));
		mot.add(new MOT("LE", "CC", 04, 0));
		mot.add(new MOT("GE", "CC", 05, 0));
		mot.add(new MOT("NE", "CC", 06, 0));
		mot.add(new MOT("ANY", "CC", 07, 0));

		/*int len = mot.size();
		for(int i=0; i<len; i++){
			MOT obj = mot.get(i);
			System.out.println(obj.mnc+" "+obj.cls+" "+obj.m_code+" "+obj.length);
		}*/

		return mot;

	}

	public static java.util.HashMap<String, Integer> Create_MOTindex(){

		hm = new java.util.HashMap<String, Integer>();
		hm.put("STOP",0);
		hm.put("ADD",1);
		hm.put("SUB",2);
		hm.put("MULT",3);
		hm.put("MOVER",4);
		hm.put("MOVEM",5);
		hm.put("COMP",6);
		hm.put("BC",7);
		hm.put("DIV",8);
		hm.put("READ",9);
		hm.put("PRINT",10);
		hm.put("START",11);
		hm.put("END",12);
		hm.put("ORIGIN",13);
		hm.put("EQU",14);
		hm.put("LTORG",15);
		hm.put("DS",16);
		hm.put("DC",17);
		hm.put("AREG",18);
		hm.put("BREG",19);
		hm.put("CREG",20);
		hm.put("EQ",21);
		hm.put("LT",22);
		hm.put("GT",23);
		hm.put("LE",24);
		hm.put("GE",25);
		hm.put("NE",26);
		hm.put("ANY",27);

		return hm;

	}

}
