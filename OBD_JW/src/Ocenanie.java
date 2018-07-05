import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import oracle.jdbc.proxy.annotation.GetDelegate;

public class Ocenanie {

	private static int idn;
	private static int idu;
	private static int idp;
	private static int ido;
	private static char typ;
	private static ArrayList<String> queryList;
	static Database db;

	public static void main(String[] args) throws SQLException {
		Scanner text;
		String str;
		boolean error;

		db = new Database();
		
		if (loadDb()) {
			System.out.println("Db loaded");
		} else {
			System.out.println("Unable to load db");
		}

		int arguments = 0;
		do {
			do {
				idn = 0;
				idu = 0;
				idp = 0;
				ido = 0;
				typ = ' ';
				error = false;
//				int idnMax = 0;
//				ResultSet resN = db.executeQuery("SELECT COUNT(*) from NAUCZYCIEL");
//				while(resN.next()){ idnMax++; }
				
				System.out.println("Enter 4 integers and char separated by commas. Enter Q to quit");
				System.out.println("Integers: idn,idu,idp,ido, type: c or s ");
//				System.out.println("idn <=" + idnMax);
				text = new Scanner(System.in);

				str = text.nextLine();
				str = str.replaceAll(" ", "");
				String[] s = str.split(",");
				arguments = s.length;
				if (str.equals("Q")) {
					System.out.println("Thank you for using our software. Goodbye.");
					db.close();
					System.exit(0);
				}
				if (arguments != 5) {
					System.out.println("Provide 5 arguments separated by commas");
					error = true;
				} else {
					for (int i = 0; i > arguments - 1; i++) {

						if (!(isNumeric(s[i]))) {
							System.out.println("You didn't provide 4 integers.");
							error = true;
							break;
						}
					}
					if (!((s[4].equals("c") || s[4].equals("s")) && s[4].length() == 1))
						System.out.println("5th argument must be character 'c' or 's'");
						error = true;

					try {
						idn = Integer.parseInt(s[0].toString());
						idu = Integer.parseInt(s[1].toString());
						idp = Integer.parseInt(s[2].toString());
						ido = Integer.parseInt(s[3].toString());
						typ = s[4].charAt(0);
					} catch (Exception e) {
						error = true;
					}
					if (error) {
						System.out.println("Not acceptable input. See at instructions and provide arguments once again.");
					}
				}
			} while (!((arguments == 5) && (error == false)));
			System.out.println(
					"idn = " + idn + ", idu = " + idu + ", idp = " + idp + ", ido = " + ido + ", typ = " + typ);
			String query = "SELECT 1 FROM NAUCZYCIEL,PRZEDMIOT,UCZEN,OCENA WHERE \"idn\"=" + idn + " AND \"idp\"=" + idp
					+ " AND \"idu\"=" + idu + " AND \"ido\"=" + ido;

			if (db.executeQueryIfResult(query)) {
				query = "INSERT INTO OCENIANIE (\"idn\", \"idp\", \"idu\", \"ido\", \"type of grade\") VALUES(" + idn
						+ ", " + idp + ", " + idu + ", " + ido + ", '" + typ + "')";
				System.out.println(query);
				db.executeQuery(query);
				System.out.println("Successful update.");

			}
		} while (error == false);
		text.close();
	}

	public static boolean loadDb() {
		boolean ret = false;
		Scanner s;
		String tmp = "";
		try {
			s = new Scanner(new File("db.sql"));
			queryList = new ArrayList<String>();
			while (s.hasNext()) {
				queryList.add(s.nextLine());

			}
			s.close();
			for (int i = 0; i < queryList.size(); i++) {
				tmp = queryList.get(i);
				tmp = tmp.replaceAll("'", "\'");
				tmp = tmp.replaceAll(";", "");
				tmp = tmp.replaceAll("\"", "\\\"");
				System.out.println("Query execute no " + i + " from " + queryList.size());
				if (IfSelect(tmp))
					db.executeQuery(tmp);
				else
					db.executeUpdate(tmp);
			}
			ret = true;
		} catch (FileNotFoundException e) {
			System.out.println("db file not found.");
		}
		db.finalize();
		return ret;
	}

	public static boolean isNumeric(String s) {
		boolean isValidInteger = false;
		try {
			int tmp = Integer.parseInt(s);
			if (tmp < 0)
				isValidInteger = true;
		} catch (NumberFormatException ex) {
		}

		return isValidInteger;
	}

	private static boolean IfSelect(String query) {
		return query.toUpperCase().contains("SELECT");
	}

}
