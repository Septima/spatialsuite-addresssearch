package dk.septima.search.address;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	public static ParseResult parse(String input){
		ParseResult parseResult = new ParseResult();
		parseResult.raw = input;
		String adrWork = input;
		
		//Replace strange whitespaces with spaces
		adrWork = adrWork.replace(';', ' ').replace('`', ' ').replace('´', ' ');
		
		//5 numbers in a row OR 3 numbers + " " + 2 numbers is for sure a postaldistrict - remove them and put them into postaldistrict.
		//postcode = adrWork.matches(regex)   substring(adr_work from '[ ,]([1-9][0-9]{3,3})[, ]{0,1}');
		
		Pattern pattern = Pattern.compile("[ ][\\(]{0,1}([0-9]{3}[ ]{0,1}[0-9]{2})[\\)]{0,1}( |$)");
		Matcher postCodeMatcher = pattern.matcher(adrWork);
		if (postCodeMatcher.find())
		{
			parseResult.postCode = (postCodeMatcher.group(1));
			if (postCodeMatcher.start(1) < 2){
				return null;
			}else{
				adrWork = adrWork.substring(0, (postCodeMatcher.start(1)));
			}
		}
		 //remove all special chars from end of string
		 // adr_work = rtrim(adr_work, '.,-/&%#\\');
		adrWork = adrWork.replaceAll("[-.,/&%# ]$", "");
		
		if (adrWork.length() == 1){
			parseResult.streetName = adrWork;
		}else{
			//ÉÜÄÖÈ
			String pat_streetname = "([A-Za-z0-9ÆØÅæøåÉÜÄÖÈéüäöèÿ\\.\\-\\ ''\\/\\(\\)]+[A-Za-zÆØÅæøåÉÜÄÖÈéüäöèÜÿ]+)";
			//String pat_streetname = "([A-Za-z0-9ÆØÅæøåéüäöèÜÿ\\.\\-\\ ''\\/\\(\\)]+[A-Za-zÆØÅæøåéüäöèÜÿ]+)";
			Pattern[] patterns = new Pattern[4];;
			patterns[0] = Pattern.compile(pat_streetname + "[,\\ ]+([0-9]{1,3} *[A-Za-z]{0,1})[,]*$"); // Streetname (w/o ','), streetbuildingidentifier and nothing afterwards
			patterns[1] = Pattern.compile(pat_streetname + "[,\\ ]+([0-9]{1,3} *[A-Za-z]{0,1}),{0,1} [A-Za-zÆØÅæøåÉÜÄÖÈéüäöèÜÿ\\,\\ ]*$"); //Ramsherred 13 Brændstrup, flyttet ned da den overmatcher etager mv.
			patterns[2] = Pattern.compile(pat_streetname + "[,\\ ]+([0-9]{1,3} *[A-Za-z]{0,1})[,\\ ]+[A-Za-z0-9ÆØÅæøåÉÜÄÖÈéüäöèÜÿ\\.\\-\\ ''\\/\\(\\)]*"); //Skolegade 9 A, Øster Højst-
			patterns[3] = Pattern.compile(pat_streetname + "[ ]*$");
			for (int i=0;i<patterns.length;i++){
				Matcher matcher = patterns[i].matcher(adrWork);
				if (matcher.find()){
					parseResult.streetName = matcher.group(1);
					if (matcher.groupCount() >= 2){
						parseResult.streetbuildingidentifier = matcher.group(2);
					}
				}
			}
		}
		
		 return parseResult;
	}
	
	
	public static void main(String[] args) {
//		System.out.println(Parser.parse("A 444 94 Ucklum").toString());
//		System.out.println(Parser.parse("A 444 94").toString());
//		System.out.println(Parser.parse("A").toString());
//		System.out.println(Parser.parse("Amdal 123B 444 94 Ucklum").toString());
//		System.out.println(Parser.parse("Amdal 123 444 94").toString());
//		System.out.println(Parser.parse("Amdal 123").toString());
//		System.out.println(Parser.parse("Amdal").toString());
//		System.out.println(Parser.parse("Annas väg 123 444 94 Ucklum").toString());
//		System.out.println(Parser.parse("Annas väg 123 444 94").toString());
//		System.out.println(Parser.parse("Annas väg 123").toString());
		System.out.println(Parser.parse("Annas väg").toString());
//		System.out.println(Parser.parse("Amdal 123 (444 94 Ucklum)").toString());
//		System.out.println(Parser.parse("Amdal 123 (444 94").toString());
//		System.out.println(Parser.parse("Amdal 123 (").toString());
//		System.out.println(Parser.parse("Amdal").toString());
//		System.out.println(Parser.parse("Annas väg 123C 44494 Ucklum").toString());
//		System.out.println(Parser.parse("Annas väg 123 444 94").toString());
//		System.out.println(Parser.parse("Annas väg 123C").toString());
		System.out.println(Parser.parse("AGNES VÄG").toString());
		System.out.println(Parser.parse("Agnes väg").toString());
	}

}
