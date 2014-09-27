import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class IKAnalyzerDemo {
	
	public static void main(String[] args) throws IOException{
		
		
		Map<String,Double> idfMap = new HashMap<String, Double>();
		//Map<String,Integer> hashMap = new HashMap<String, Integer>();
		Map<String, Integer> wordMap = new HashMap<String, Integer>();	//词和序号的map
		ArrayList<String> wordArrayList = new ArrayList<String>();
		ArrayList<Integer> numPostPerTheme = new ArrayList<Integer>();
		Map<Integer, Integer> postToThemeMap = new HashMap<Integer, Integer>();	//tfidf矩阵的行对应的主题
		ArrayList<String> postArrayList = new ArrayList<String>();
		
		
		int wordMapIndex=0;		//词的索引结构，最后得到的是词数
		int countPost=0;
		int countTheme=10;
		//int postIndex = 0;
		String str = "啊测试分词工具一些停止词";
		String directory = "data\\";
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
        
		
		for(int i=0;i<post.length;i++){			//得到一个词-序号的map
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
			int postPerTheme=0;
	        while(input.hasNext()){
	        	postPerTheme++;
	        	postToThemeMap.put(countPost, i);
	        	countPost++;
	        	str = input.nextLine();
	        	postArrayList.add(str);
	        	StringReader reader = new StringReader(str);
	        	IKSegmenter ik = new IKSegmenter(reader,true);
	        	
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
	        numPostPerTheme.add(postPerTheme);
	        //System.out.println(i+"类中有"+postPerTheme+"个帖子");
	        input.close();
		}
		
		double[][] tfidfMatrix = new double[countPost][wordMapIndex];
		for(int i = 0;i<countPost;i++)
			for(int j = 0;j<wordMapIndex;j++)
				tfidfMatrix[i][j] = 0;
		
		for(int i = 0;i<postArrayList.size();i++){
			String string = postArrayList.get(i);
			StringReader reader = new StringReader(string);
			IKSegmenter ik = new IKSegmenter(reader, true);
			Lexeme lx = null;
			while((lx = ik.next())!=null){
				String word = lx.getLexemeText();
				int column = wordMap.get(word).intValue();
				tfidfMatrix[i][column] = tfidfMatrix[i][column]+1;
			}
		}
		/*
		for(int i=0;i<post.length;i++){		//	得到词频数的矩阵
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
			
			while(input.hasNext()){
				postToThemeMap.put(postIndex, i);
				postIndex++;
				str = input.nextLine();
				StringReader reader = new StringReader(str);
				IKSegmenter ik = new IKSegmenter(reader,true);
				Lexeme lexeme = null;
	        	while((lexeme = ik.next())!=null){
	        		String word = lexeme.getLexemeText();
	        		
	        		int column = wordMap.get(word).intValue();
	        		tfidfMatrix[postIndex-1][column]=tfidfMatrix[postIndex-1][column]+1;
	        	}
			}
			input.close();
		}
		*/
		System.out.println("-----------"+countPost+"  "+wordMapIndex+"-------------"+wordArrayList.size());
		for(int j=0;j<wordMapIndex;j++){			//得到每个词在多少个帖子中出现过，以用来计算idf的值。
			String word = wordArrayList.get(j);
			if(!idfMap.containsKey(word)){
				idfMap.put(word, 0.0);
			}
			double sum = 0;
			for(int i=0;i<countPost;i++){
				if(tfidfMatrix[i][j]>0)
					sum = sum+1;
			}
			idfMap.put(word, sum);
		}
		
		Set<String> set = idfMap.keySet();
		
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext()){		//计算每个词的idf值
			String word = iterator.next();
			
			double d = idfMap.get(word).doubleValue();
			d=Math.log((countPost)/(1+d));
			
			idfMap.put(word, d);
			
		}
		System.out.println("计算完idf 了");
		/*
		 * 	10交叉验证
		 * 	
		 */
		ArrayList<Double> correctNumNBD = new ArrayList<Double>();
		ArrayList<Double> correctNumNBCD = new ArrayList<Double>();
		ArrayList<Double> correctNumNBCG = new ArrayList<Double>();
		
		for(int k = 0;k<10;k++){				
			Map<Integer, Integer> testPostThemeMap = new HashMap<Integer, Integer>();
			Map<Integer, Integer> trainPostThemeMap = new HashMap<Integer, Integer>();
			
			Map<Integer, Integer> testThemePostNumMap = new HashMap<Integer, Integer>();
			Map<Integer, Integer> trainThemePostNumMap = new HashMap<Integer, Integer>();
			
			int testTotalPost = countPost/10;
			int trainTotalPost = countPost-testTotalPost;
			double[][] trainMatrix = new double[trainTotalPost][wordMapIndex];
			int flagTestRow = 0;
			int flagTrainRow = 0;
			double[][] testMatrix = new double[testTotalPost][wordMapIndex];
			for(int i = 0;i<countPost;i++){		
				//System.out.println(k+" ---- "+i);
				if((i%10)==k){			//测试集
					for(int j = 0;j<wordMapIndex;j++){
						//System.out.println(i+"  "+testTotalPost+"   "+flagTestRow);
						testMatrix[flagTestRow][j] = tfidfMatrix[i][j];  
						
					}
					int whichTheme = postToThemeMap.get(i).intValue();
					testPostThemeMap.put(flagTestRow, whichTheme);
					if(testThemePostNumMap.containsKey(whichTheme)){
						int numPost = testThemePostNumMap.get(whichTheme).intValue();
						numPost = numPost+1;
						testThemePostNumMap.put(whichTheme, numPost);
					}
					else{
						testThemePostNumMap.put(whichTheme, 1);
					}
					flagTestRow++;
				}
				else{					//训练集
					for(int j = 0;j<wordMapIndex;j++){
						trainMatrix[flagTrainRow][j] = tfidfMatrix[i][j];
					}
					int whichTheme = postToThemeMap.get(i).intValue();
					trainPostThemeMap.put(flagTrainRow, whichTheme);
					if(trainThemePostNumMap.containsKey(whichTheme)){
						int numPost = trainThemePostNumMap.get(whichTheme).intValue();
						numPost = numPost+1;
						trainThemePostNumMap.put(whichTheme, numPost);
						//System.out.println("whichTheme:"+whichTheme+" "+numPost);
					}
					else{
						trainThemePostNumMap.put(whichTheme, 1);
					}
					flagTrainRow++;
				}
			}
			
			//计算NBD
			
			NaiveBayes nbd = new NaiveBayes(trainTotalPost, wordMapIndex, countTheme);
			nbd.setNumBoardMap();
			for(int index=0;index<countTheme;index++){
				int value = trainThemePostNumMap.get(index);
				//System.out.println(value);
				nbd.setNumPerTheme(value);
			}
			nbd.calcPriorProbability();
			nbd.discreteContinuousAttributeNBD(trainMatrix);	
			double sumNBD = 0;
			for(int i = 0;i<testTotalPost;i++){
				Map<Integer, Double> wordsAppearMap = new HashMap<Integer, Double>();
				for(int j = 0;j<wordMapIndex;j++){
					if(testMatrix[i][j]>0)
						wordsAppearMap.put(j, 1.0);
				}
				int classTheme=nbd.classsifyUseTFNBD(wordsAppearMap);
				int realTheme = testPostThemeMap.get(i).intValue();
				if(classTheme==realTheme){
					sumNBD = sumNBD+1.0;
				}
			}
			double correctRatioNBD = sumNBD/testTotalPost;
			System.out.println("第"+k+"次计算出来的NBD平均准确率为"+correctRatioNBD);
			correctNumNBD.add(correctRatioNBD);
			
			
			//计算训练集中tf的值，把频数转换成频率
			for(int i = 0;i<trainTotalPost;i++){
				double wordsPerPost = 0;
				
				for(int j = 0;j<wordMapIndex;j++)
					wordsPerPost = trainMatrix[i][j]+wordsPerPost;
				for(int j = 0;j<wordMapIndex;j++)
					trainMatrix[i][j] = trainMatrix[i][j]/wordsPerPost;
				/*
				double max = trainMatrix[i][0];
				for(int j=0;j<wordMapIndex;j++){
					if(max<trainMatrix[i][j])
						max = trainMatrix[i][j];
				}
				for(int j=0;j<wordMapIndex;j++){
					trainMatrix[i][j] = 0.5+(0.5*trainMatrix[i][j])/max;
					//if(trainMatrix[i][j]>0.5)
						//System.out.println("训练集中"+wordsPerPost+"--"+trainMatrix[i][j]);
				}
				*/
			}
			
			
			//计算测试集中的tf的值，把频数转换成频率
			for(int i=0;i<testTotalPost;i++){
				double sumWords = 0;
				
				for(int j = 0;j<wordMapIndex;j++)
					sumWords = testMatrix[i][j]+sumWords;
				for(int j = 0;j<wordMapIndex;j++)
					testMatrix[i][j] = testMatrix[i][j]/sumWords;
				/*
				double max = testMatrix[i][0];
				for(int j = 0;j<wordMapIndex;j++){
					if(max<testMatrix[i][j])
						max=testMatrix[i][j];
				}
				
				for(int j = 0;j<wordMapIndex;j++){
					testMatrix[i][j] = 0.5+(0.5*testMatrix[i][j])/max;
					
				}*/
				
			}
			
			for(int i=0;i<trainTotalPost;i++){		//计算训练集中tf-idf的值。
				for(int j=0;j<wordMapIndex;j++){
					String word = wordArrayList.get(j);
					double idf = idfMap.get(word).doubleValue();
					
					trainMatrix[i][j]=idf*trainMatrix[i][j];
					//System.out.println("训练集tf-idf：" + idf+"  "+trainMatrix[i][j]);
				}
			}
			//计算测试集中tfidf的值
			for(int i = 0;i<testTotalPost;i++){
				for(int j=0;j<wordMapIndex;j++){
					String word = wordArrayList.get(j);
					double idf = idfMap.get(word).doubleValue();
					testMatrix[i][j] = idf*testMatrix[i][j];
				}
			}
			
			//训练NBCD
			NaiveBayes nbcd = new NaiveBayes(trainTotalPost, wordMapIndex, countTheme);
			nbcd.setNumBoardMap();
			for(int index=0;index<countTheme;index++){
				int value = trainThemePostNumMap.get(index).intValue();
				nbcd.setNumPerTheme(value);
			}
			
			nbcd.calcPriorProbability();
			nbcd.discreteContinuousAttributeNBCD(trainMatrix);
			
			double sumNBCD = 0;
			for(int i = 0;i<testTotalPost;i++){
				Map<Integer, Double> wordsAppearMap = new HashMap<Integer, Double>();
				for(int j = 0;j<wordMapIndex;j++){
					if(testMatrix[i][j]>0)
						wordsAppearMap.put(j, testMatrix[i][j]);
				}
				int classTheme=nbcd.classifyUseDiscreteNBCD(wordsAppearMap);
				int realTheme = testPostThemeMap.get(i).intValue();
				if(classTheme==realTheme){
					sumNBCD = sumNBCD+1.0;
				}
			}
			double correctRatioNBCD = sumNBCD/testTotalPost;
			System.out.println("第"+k+"次计算出来的NBCD平均准确率为"+correctRatioNBCD);
			correctNumNBCD.add(correctRatioNBCD);
			
			
			//计算NBCG
			NaiveBayes nbcg = new NaiveBayes(trainTotalPost, wordMapIndex, countTheme);
			nbcg.setNumBoardMap();
			for(int index=0;index<countTheme;index++){
				int value = trainThemePostNumMap.get(index).intValue();
				nbcg.setNumPerTheme(value);
			}
			nbcg.calcPriorProbability();
			//nbcg.setWordsMatrix(trainMatrix);
			nbcg.gaussianDistribution(trainMatrix);
			double sumNBCG = 0;
			
				
			for(int i = 0;i<testTotalPost;i++){
				Map<Integer, Double> wordsAppearMap = new HashMap<Integer, Double>();
				for(int j = 0;j<wordMapIndex;j++){
					if(testMatrix[i][j]>0)
						wordsAppearMap.put(j, testMatrix[i][j]);
				}
				int classTheme=nbcg.classifyUseGaussianNBCG(wordsAppearMap);
				int realTheme = testPostThemeMap.get(i).intValue();
				if(classTheme==realTheme){
					sumNBCG = sumNBCG+1.0;
				}
			}
			double correctRatioNBCG = sumNBCG/testTotalPost;
			System.out.println("第"+k+"次计算出来的NBCG平均准确率为"+correctRatioNBCG);
			correctNumNBCG.add(correctRatioNBCG);
			
		}
		double sumNBD = 0;
		double sumNBCD = 0;
		double sumNBCG = 0;
		for(int i = 0;i<correctNumNBD.size();i++){
			sumNBD = sumNBD+ correctNumNBD.get(i);
			sumNBCD = sumNBCD + correctNumNBCD.get(i);
			sumNBCG = sumNBCG + correctNumNBCG.get(i);
		}
		
		double averageNBD = sumNBD/correctNumNBD.size();
		double averageNBCD = sumNBCD/correctNumNBD.size();
		double averageNBCG = sumNBCG/correctNumNBD.size();
		sumNBD = 0;
		sumNBCD = 0;
		sumNBCG = 0;
		for(int i = 0;i<10;i++){
			double v1=correctNumNBD.get(i);
			double v2 = correctNumNBCD.get(i);
			double v3 = correctNumNBCG.get(i);
			sumNBD = sumNBD + (v1-averageNBD)*(v1-averageNBD);
			sumNBCD = sumNBCD + (v2-averageNBCD)*(v2- averageNBCD);
			sumNBCG = sumNBCG + (v3-averageNBCG)*(v3-averageNBCG);
		}
		double varianceNBD = sumNBD/correctNumNBD.size();
		double varianceNBCD = sumNBCD/correctNumNBD.size();
		double varianceNBCG = sumNBCG/correctNumNBD.size();
		System.out.println("NBD 平均值为"+averageNBD+",方差为 "+varianceNBD);
		System.out.println("NBCD 平均值为"+averageNBCD+",方差为 "+varianceNBCD);
		System.out.println("NBCG 平均值为"+averageNBCG+",方差为 "+varianceNBCG);
		System.out.println("--运算完成--");
		
		/*
		for(int i = 0;i<countPost;i++){				//计算词的频率，使用公式0.5+(0.5*f(w))/max(f(w))
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
		
		
		
		
		
		for(int i=0;i<countPost;i++){		//计算tfidfMatrix的值。
			for(int j=0;j<wordMapIndex;j++){
				String word = wordArrayList.get(j);
				double idf = idfMap.get(word).doubleValue();
				tfidfMatrix[i][j]=idf*tfidfMatrix[i][j];
				System.out.println(tfidfMatrix[i][j]);
			}
		}
		
		
		
		System.out.println("运算完成");
		*/
		
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
		        IKSegmentation ik = new IKSegmentation(reader, true);// 锟斤拷为true时锟斤拷锟街达拷锟斤拷锟斤拷锟斤拷锟斤拷锟绞筹拷锟叫凤拷  
		        
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
			d=(Math.log(d))/(Math.log(2));		//锟斤拷锟斤拷锟斤拷锟街�
			idfMap.put(str, d);
			outIDF.println(str+"\t"+d);
		}
		
		outIDF.close();
		*/
	}
	
	
	
}
