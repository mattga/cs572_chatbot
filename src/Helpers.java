import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Helpers {
	public static boolean isRepetition(String str) {
		return str.equals("REPETITION T1") || str.equals("REPETITION T2") || str.equals("NULL INPUT REPETITION");
	}

	public static boolean isBotDontUnderstood(String str) {
		return str.equals("BOT DONT UNDERSTAND");
	}

	public static boolean isQuestionAnswer(String str) {
		for (String ans : Config.QuestionAnswers) {
			if (levenshtein(str, ans) > Config.SimilarityThreshhold) {
				return true;
			}
		}

		return false;
	}

	public static String cleanse(String str)
	{
		String clean=str.toUpperCase();//change all letters to upper ones
		clean=clean.replaceAll(" +", " ");//remove extra space
		clean=clean.replaceAll("[\\pP��������]", "");//remove all punctuations
		return clean;
	}

	public static String removeStopwords(String str) {
		String res = "";

		for (String word : cleanse(str).split(" ")) {
			boolean isStopWord = false;

			for (String stopword : Config.Stopwords) {
				if (word.equals(stopword.toUpperCase())) {
					isStopWord = true;
					break;
				}
			}

			if (!isStopWord) {
				res += word + " ";
			}
		}

		if (res.charAt(res.length()-1) == ' ') {
			return cleanse(res.substring(0, res.length()-1));
		}
		else 
		{
			return cleanse(res);
		}
	}

	public static float levenshtein(String str1, String str2) {  
		//���������ַ����ĳ��ȡ�  
		int len1 = str1.length();  
		int len2 = str2.length();  
		//��������˵�����飬���ַ����ȴ�һ���ռ�  
		int[][] dif = new int[len1 + 1][len2 + 1];  
		//����ֵ������B��  
		for (int a = 0; a <= len1; a++) {  
			dif[a][0] = a;  
		}  
		for (int a = 0; a <= len2; a++) {  
			dif[0][a] = a;  
		}  
		//���������ַ��Ƿ�һ�����������ϵ�ֵ  
		int temp;  
		for (int i = 1; i <= len1; i++) {  
			for (int j = 1; j <= len2; j++) {  
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {  
					temp = 0;  
				} else {  
					temp = 1;  
				}  
				//ȡ����ֵ����С��  
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
						dif[i - 1][j] + 1);  
			}
		}
		//System.out.println("�ַ���\""+str1+"\"��\""+str2+"\"�ıȽ�");  
		//ȡ�������½ǵ�ֵ��ͬ����ͬλ�ô���ͬ�ַ����ıȽ�  
		//System.out.println("���첽�裺"+dif[len1][len2]);  
		//�������ƶ�  
		float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());  
		//System.out.println("���ƶȣ�"+similarity);  
		return similarity;
	} 

	static int min(int a, int b, int c) {  
		if(a < b) {  
			if(a < c)  
				return a;  
			else   
				return c;  
		} else {  
			if(b < c)  
				return b;  
			else   
				return c;  
		}  
	}  

	public static String readUnknown() throws IOException  {
		File unknown = new File(Config.unknownFile);
		File temp = new File(Config.tempFile);
		
		BufferedReader fin = new BufferedReader(new FileReader(unknown));
		BufferedWriter fout = new BufferedWriter(new FileWriter(temp, false));
		String _line, line  = fin.readLine();
		
		while ((_line = fin.readLine()) != null) {
			fout.append(_line + "\n");
		}
		
		fin.close();
		fout.close();
		
		temp.renameTo(unknown);

		return line;
	}

	public static void writeUnknown(String keywords) throws IOException {
		BufferedWriter fout = new BufferedWriter(new FileWriter(Config.unknownFile, true));
		fout.append(keywords.toUpperCase() + "\n");
		fout.close();
	}

	public static void learnUnknown(String input, String resp) throws IOException {
		//		input = removeStopwords(input);

		for (int i = 0; i < Config.KB.length; i++) {
			for (int j = 0; j < Config.KB[i][0].length; j++) {
				if (levenshtein(Config.KB[i][0][j], input) > Config.SimilarityThreshhold) {
					// Add input keywords
					String kwords[] = new String[Config.KB[i][0].length+1];
					for (int k = 0; k < Config.KB[i][0].length; k++) {
						kwords[k] = Config.KB[i][0][k];
					}
					kwords[Config.KB[i][0].length] = cleanse(input);
					Config.KB[i][0] = kwords;
					
					// Add response
					String resps[] = new String[Config.KB[i][1].length+1];
					for (int k = 0; k < Config.KB[i][1].length; k++) {
						resps[k] = Config.KB[i][1][k];
					}
					resps[Config.KB[i][1].length] = resp;
					Config.KB[i][1] = resps;
					Config.WriteKB();
					
					return;
				}
			}
		}

		// Add new entry
		String newEntry[][] = new String[2][];
		newEntry[0] = new String[1];
		newEntry[1] = new String[1];
		newEntry[0][0] = cleanse(input);
		newEntry[1][0] = resp.toUpperCase();

		Config.WriteToKB(newEntry);
	}
}
