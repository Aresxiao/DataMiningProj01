import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;


import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;



public class IKAnalyzerDemo {
	
	public static void main(String[] args) throws IOException{
		
		/*String string = "你好";
		IKAnalyzerDemo demo = new IKAnalyzerDemo();
		boolean b = demo.containChar(string);
		System.out.println(b);
		*/
		
		Map<String,Double> idfMap = new HashMap<String, Double>();
		Map<String,Integer> hashMap = new HashMap<String, Integer>();
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		ArrayList<String> wordArrayList = new ArrayList<String>();
		
		int wordMapIndex=0;		//序列索引，最后得到的是总词数
		int countPost=0;
		int postIndex = 0;
		String str = " ";
		String directory = "lily\\";
		String basketball=directory+"Basketball.txt";
		String computer=directory+"D_Computer.txt";
		String fleaMarket = directory+"FleaMarket.txt";
		String girls = directory + "Girls.txt";
		String jobExpress = directory+"JobExpress.txt";
		String mobile = directory + "Mobile.txt";
		String stock = directory + "Stock.txt";
		String suggestion = directory+"V_Suggestions.txt";
		String warAndPeace = directory+"WarAndPeace.txt";
		String WorldFootball = directory + "WorldFootball.txt";
		
		String[] post = {basketball,computer,fleaMarket,girls,jobExpress,mobile,stock,suggestion,
				warAndPeace,WorldFootball};
		for(int i=0;i<post.length;i++){			//得到所有词的链表和词-序号映射
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
	        while(input.hasNext()){
	        	countPost++;
	        	str = input.nextLine();
	        	StringReader reader = new StringReader(str);
	        	IKSegmentation ik = new IKSegmentation(reader,true);
	        	
	        	Lexeme lexeme = null;
	        	while((lexeme = ik.next())!=null){
	        		String word = lexeme.getLexemeText();
	        		
	        		if(!wordMap.containsKey(word)){
	        			wordMap.put(word, wordMapIndex);
	        			wordArrayList.add(word);
	        			wordMapIndex++;
	        		}
	        	}
	        }
	        input.close();
		}
		
		double[][] tfidfMatrix = new double[countPost][wordMapIndex];
		for(int i = 0;i<countPost;i++)
			for(int j = 0;j<wordMapIndex;j++)
				tfidfMatrix[i][j] = 0;
		
		
		for(int i=0;i<post.length;i++){		//得到词频数矩阵
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				postIndex++;
				str = input.nextLine();
				StringReader reader = new StringReader(str);
				IKSegmentation ik = new IKSegmentation(reader,true);
				Lexeme lexeme = null;
	        	while((lexeme = ik.next())!=null){
	        		String word = lexeme.getLexemeText();
	        		if(!wordMap.containsKey(word)){
	        			System.out.println(word+" 没有出现");
	        		}
	        		int column = wordMap.get(word).intValue();
	        		/*if(tfidfMatrix[postIndex-1][row]==0){
	        			if(idfMap.containsKey(word)){
	        				double v = idfMap.get(word).doubleValue();
	        				v=v+1.0;
	        				idfMap.put(word, v+1);
	        			}
	        			else {
							idfMap.put(word, 1.0);
						}
	        		}*/
	        		tfidfMatrix[postIndex-1][column]=tfidfMatrix[postIndex-1][column]+1;
	        	}
			}
			input.close();
		}
		
		
		for(int j=0;j<wordMapIndex;j++){
			String word = wordArrayList.get(j);
			if(!idfMap.containsKey(word)){
				idfMap.put(word, 0.0);
			}
			for(int i=0;i<countPost;i++){
				if(tfidfMatrix[i][j]>0){
					double v = idfMap.get(word).doubleValue();
					v=v+1.0;
					
					idfMap.put(word, v);
				}
			}
		}
		
		for(int i = 0;i<countPost;i++){				//得到一个词频矩阵，计算方法是0.5+(0.5*f(w))/max(f(w))
			double wordsperPost = 0;
			for(int j = 0;j<wordMapIndex;j++){
				wordsperPost = wordsperPost + tfidfMatrix[i][j];
			}
			for(int j = 0;j<wordMapIndex;j++){
				tfidfMatrix[i][j] = tfidfMatrix[i][j]/wordsperPost;
			}
			double max=tfidfMatrix[i][0];
			for(int j = 0;j<wordMapIndex;j++){
				if(max<tfidfMatrix[i][j])
					max = tfidfMatrix[i][j];
			}
			for(int j = 0;j<wordMapIndex;j++){
				tfidfMatrix[i][j] = 0.5+(0.5*tfidfMatrix[i][j])/max;
			}
		}
		
		
		
		
		Set<String> set = idfMap.keySet();
		
		Iterator iterator = set.iterator();
		while(iterator.hasNext()){		//计算每个词的idf值
			String word = iterator.next().toString();
			double d = idfMap.get(word).doubleValue();
			d=Math.log(countPost)/Math.log(1+d);
			idfMap.put(word, d);
		}
		
		//File file = new File(directory+"tfidf.txt");
		//PrintWriter pw = new PrintWriter(file);
		for(int i=0;i<countPost;i++){		//计算tf-idf的值，存放在tfidfMatrix矩阵中。
			for(int j=0;j<wordMapIndex;j++){
				String word = wordArrayList.get(j);
				double idf = idfMap.get(word).doubleValue();
				tfidfMatrix[i][j]=idf*tfidfMatrix[i][j];
				System.out.println(tfidfMatrix[i][j]);
				//pw.print(tfidfMatrix[i][j]+" ");
			}
			//pw.println();
		}
		//pw.close();
		System.out.println("运算完成");
		
		
		//System.out.println(countPost+"\t"+wordMapIndex+"\t"+tf[0].length);
		
		
		/*
		for(int i=0;i<post.length;i++){
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
			File of = new File(directory+i+"tf.txt");
	        PrintWriter pw = new PrintWriter(of);
	        
			while(input.hasNext()){
				countPost++;
				str = input.nextLine();
				//System.out.println(str);
				StringReader reader = new StringReader(str);  
		        IKSegmentation ik = new IKSegmentation(reader, true);// 当为true时，分词器进行最大词长切分  
		        
		        Lexeme lexeme = null;  
		        while ((lexeme = ik.next()) != null){  
		        	String word = lexeme.getLexemeText();
		        	if(hashMap.containsKey(word)){
		        		int v = hashMap.get(word).intValue();
		        		v++;
		        		hashMap.put(word, v);
		        	}
		        	else{
		        		if(idfMap.containsKey(word)){
		        			double v = idfMap.get(word).doubleValue();
		        			v=v+1.0;
		        			idfMap.put(word, v);
		        		}
		        		else {
		        			idfMap.put(word, 1.0);
						}
		        		hashMap.put(word, 1);
		        	}
		            //System.out.println(lexeme.getLexemeText());  
		        }
		        
				Set<String> set=hashMap.keySet();
				Iterator iteratorKey = set.iterator();
				float sum=0;
				while(iteratorKey.hasNext()){
					sum = sum+hashMap.get(iteratorKey.next().toString()).intValue();
				}
				iteratorKey = set.iterator();
				while(iteratorKey.hasNext()){
					String string = iteratorKey.next().toString();
					float f = (float)(hashMap.get(string).intValue())/(float)sum;
					string = string+"\t";
					
					pw.print(string);
					pw.println(f);
					System.out.println(string+f);
				}
				pw.println("------------------------");
			}
			pw.close();
		}
		File idfFile = new File(directory+"idf.txt");
		PrintWriter outIDF = new PrintWriter(idfFile);
		Set<String> set = idfMap.keySet();
		Iterator iteratorKey = set.iterator();
		while(iteratorKey.hasNext()){
			str = iteratorKey.next().toString();
			double d = idfMap.get(str).doubleValue();
			d=d/countPost;
			d=(Math.log(d))/(Math.log(2));		//计算对数值
			idfMap.put(str, d);
			outIDF.println(str+"\t"+d);
		}
		
		outIDF.close();
		*/
	}
	
	
	
}
