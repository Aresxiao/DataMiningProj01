import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class NaiveBayes {
	
	ArrayList<Double> arrayPriorProbability;	//先验概率
	ArrayList<Integer> numPostEveryTheme;		//每个分类帖子的数目
	ArrayList<Integer> numWordsEveryTheme;		//每个分类词的个数
	int totalPost;							//总的帖子数目
	double[][] perWordProbability;			//每个词在每个分类上的条件概率
	int totalKeywords;						//总的关键词数目
	int totalTheme;							//总的主题数目
	Map<Integer,String> numBoardMap;			//
	
	double[][] averagePerWord;				//平均数矩阵
	double[][] variancePerWord;				//方差矩阵
	double[][][] discreteProbMatrix;			//离散化连续属性保存概率
	int numPossible=2;
	
	public NaiveBayes(int countPost,int countKeywords,int countTheme){
		arrayPriorProbability = new ArrayList<Double>();
		numPostEveryTheme = new ArrayList<Integer>();
		totalKeywords = countKeywords;
		totalPost = countPost;
		totalTheme = countTheme;
		perWordProbability = new double[totalTheme][totalKeywords];
		averagePerWord = new double[totalTheme][totalKeywords];
		variancePerWord = new double[totalTheme][totalKeywords];
		discreteProbMatrix = new double[totalTheme][totalKeywords][numPossible];
		numBoardMap = new HashMap<Integer, String>();
		initial();
	}
	
	void initial(){
		for(int i = 0;i<totalTheme;i++)
			for(int j = 0;j<totalKeywords;j++)
				perWordProbability[i][j]=0;
	}
	
	
	public void setNumBoardMap(){
		numBoardMap.put(0, "Basketball");
		numBoardMap.put(1, "D_Computer");
		numBoardMap.put(2, "FleaMarket");
		numBoardMap.put(3, "Girls");
		numBoardMap.put(4, "JobExpress");
		numBoardMap.put(5, "Mobile");
		numBoardMap.put(6, "Stock");
		numBoardMap.put(7, "V_Suggestions");
		numBoardMap.put(8, "WarAndPeace");
		numBoardMap.put(9, "WorldFootball");
	}
	
	public void setNumPerTheme(int sum){
		numPostEveryTheme.add(sum);
	}
	
	public void setTotalPost(int countPost){
		this.totalPost = countPost;
	}
	
	public void calcPriorProbability(){		//计算先验概率
		for(int i = 0;i<totalTheme;i++){
			double d = numPostEveryTheme.get(i).doubleValue()/totalPost;
			arrayPriorProbability.add(d);
		}
		return ;
	}
	
	
	double getCountWordsPerClass(int i){
		double sum=0;
		return sum;
	}
	public void setWordsMatrix(double[][] matrix){
		System.out.println("---"+totalPost+"---" +totalKeywords+" ---");
		for(int i = 0;i < totalPost;i++){
			
			for(int j = 0;j < totalKeywords;j++){
				perWordProbability[i][j] = matrix[i][j];
			}
		}
	}
	/*
	 *  @param tfidfMatrix矩阵是一个频数矩阵，根据矩阵每个词的条件概率，并存放在perWordProbability矩阵中
	 *  
	 */
	public void discreteContinuousAttributeNBD(double[][] tfidfMatrix){		
		
		Iterator<Integer> iterator = numPostEveryTheme.iterator();
		int flagRow = 0;
		int flagTheme=0;
		while(iterator.hasNext()){
			int numPost = iterator.next().intValue();
			for(int j=0;j<totalKeywords;j++){
				for(int i=flagRow;i<(flagRow+numPost);i++){
					perWordProbability[flagTheme][j]=perWordProbability[flagTheme][j]+tfidfMatrix[i][j];
				}
			}
			flagTheme++;
			flagRow = flagRow+numPost;
		}
		
		for(int i=0;i<totalTheme;i++){
			double sum=0;
			for(int j=0;j<totalKeywords;j++){
				sum=sum+perWordProbability[i][j];
			}
			for(int j=0;j<totalKeywords;j++)
				perWordProbability[i][j] = (perWordProbability[i][j]+0.001)/(sum+(double)(totalKeywords)*0.001);
		}
	}
	
	/*
	 * @param 传入的tfidfMatrix是一个计算好tfidf值得矩阵，值是连续的，需要对其进行离散化。
	 * 计算得到的
	 */
	
	public int discreteProceudre(double x){
		if(x<4)
			return 0;
		
		else {
			return 1;
		}
	}
	
	public void discreteContinuousAttributeNBCD(double[][] tfidfMatrix){
		Iterator<Integer> iterator = numPostEveryTheme.iterator();
		int flagRow = 0;
		int flagTheme = 0;
		while(iterator.hasNext()){
			int numPost = iterator.next().intValue();
			for(int j=0;j<totalKeywords;j++){
				for(int i = flagRow;i<(flagRow+numPost);i++){
					perWordProbability[flagTheme][j]=perWordProbability[flagTheme][j]+tfidfMatrix[i][j];
				}
			}
			flagRow = flagRow+numPost;
			flagTheme++;
		}
		for(int i = 0;i<totalTheme;i++){
			double sum =0;
			for(int j=0;j<totalKeywords;j++)
				sum = sum + perWordProbability[i][j];	
			for(int j=0;j<totalKeywords;j++)
				perWordProbability[i][j] = (perWordProbability[i][j]+0.00001)/(sum+totalKeywords*0.00001);
		}
	}
	
	/*
	public void discreteContinuousAttributeNBCD(double[][] tfidfMatrix){
		for(int i = 0;i<totalTheme;i++)
			for(int j = 0;j<totalKeywords;j++)
				for(int k = 0;k<numPossible;k++)
					discreteProbMatrix[i][j][k]=0;
		
		Iterator<Integer> iterator = numPostEveryTheme.iterator();
		int flagRow = 0;
		int flagTheme = 0;
		while(iterator.hasNext()){
			int numPost = iterator.next().intValue();
			for(int j=0;j<totalKeywords;j++){
				//double sum = 0;
				for(int i=flagRow;i<(flagRow+numPost);i++){
					int discreteValue = discreteProceudre(tfidfMatrix[i][j]);
					discreteProbMatrix[flagTheme][j][discreteValue]=
							discreteProbMatrix[flagTheme][j][discreteValue]+1;
					//perWordProbability[flagTheme][j] = perWordProbability[flagTheme][j]+discreteValue;
				}
			}
			flagRow = flagRow+numPost;
			flagTheme++;
		}
		for(int i = 0;i<totalTheme;i++){
			
			for(int j = 0;j<totalKeywords;j++){
				double sum = 0;
				for(int k = 0;k<numPossible;k++)
					sum = sum+discreteProbMatrix[i][j][k];
				for(int k = 0;k<numPossible;k++)
					discreteProbMatrix[i][j][k] = (discreteProbMatrix[i][j][k]+0.001)/(sum+0.001*numPossible);
			}
		}
	}
	*/
	public void gaussianDistribution(double[][] tfidfMatrix){		//高斯分布
		Iterator<Integer> iterator = numPostEveryTheme.iterator();
		int flagRow=0;
		int flagTheme=0;
		while(iterator.hasNext()){
			int numPost=iterator.next().intValue();
			for(int j = 0;j<totalKeywords;j++){
				double average=0;
				
				double sum=0;
				for(int i = flagRow;i<(flagRow+numPost);i++){
					sum+=tfidfMatrix[i][j];
				}
				average = sum/numPost;
				sum=0;
				double variance=0;
				for(int i = flagRow;i<(flagRow+numPost);i++){
					sum=sum+(tfidfMatrix[i][j]-average)*(tfidfMatrix[i][j]-average);
				}
				variance = sum/numPost+0.00001;		//加上一个极小量，以防为0.
				
				averagePerWord[flagTheme][j] = average;
				variancePerWord[flagTheme][j] = variance;
				
			}
			flagRow = flagRow+numPost;
			flagTheme++;
		}
	}
	
	public int classsifyUseTFNBD(Map<Integer, Double> map){
		double[] probClassify = new double[totalTheme];
		for(int i = 0;i<totalTheme;i++){
			probClassify[i] = 0;
		}
		for(int i=0;i<totalTheme;i++){
			Set<Integer> set = map.keySet();
			Iterator<Integer> iterator = set.iterator();
			while(iterator.hasNext()){
				int column = iterator.next();
				probClassify[i]=probClassify[i]+Math.log(perWordProbability[i][column]);
			}
			probClassify[i] = probClassify[i]+Math.log(arrayPriorProbability.get(i).doubleValue());
			
		}
		int max=0;
		for(int i=0;i<totalTheme;i++){
			if(probClassify[max]<probClassify[i])
				max=i;
		}
		return max;
	}
	
	public int classifyUseDiscreteNBCD(Map<Integer, Double> map){
		double[] probClassify = new double[totalTheme];
		for(int i = 0;i<totalTheme;i++){
			probClassify[i] = 0;
		}
		for(int i=0;i<totalTheme;i++){
			Set<Integer> set = map.keySet();
			Iterator<Integer> iterator = set.iterator();
			while(iterator.hasNext()){
				int column = iterator.next();
				probClassify[i]=probClassify[i]+Math.log(perWordProbability[i][column]);
			}
			probClassify[i] = probClassify[i]+Math.log(arrayPriorProbability.get(i).doubleValue());
			
		}
		int max=0;
		for(int i=0;i<totalTheme;i++){
			if(probClassify[max]<probClassify[i])
				max=i;
		}
		return max;
	}
	
	/*
	public int classifyUseDiscreteNBCD(Map<Integer,Double> map){
		double[] probClassify = new double[totalTheme];
		for(int i = 0;i<totalTheme;i++){
			probClassify[i] = 0;
		}
		for(int i = 0;i<totalTheme;i++){
			Set<Integer> set = map.keySet();
			Iterator<Integer> iterator = set.iterator();
			while(iterator.hasNext()){
				int column = iterator.next();
				double tfidf = map.get(column);
				int discreteValue = discreteProceudre(tfidf);
				
				probClassify[i] = probClassify[i]+Math.log(discreteProbMatrix[i][column][discreteValue]);
			}
			probClassify[i] = probClassify[i]+Math.log(arrayPriorProbability.get(i).doubleValue());
		}
		int max = 0;
		for(int i = 0;i<totalTheme;i++){
			if(probClassify[max]<probClassify[i])
				max=i;
		}
		return max;
	}
	*/
	public int classifyUseGaussianNBCG(Map<Integer, Double> map){		//NBCG
		double[] probClassify = new double[totalTheme];
		for(int i = 0;i<totalTheme;i++){
			probClassify[i] = 0;
		}
		
		for(int i=0;i<totalTheme;i++){
			Set<Integer> set = map.keySet();
			Iterator<Integer> iterator = set.iterator();
			while(iterator.hasNext()){
				int value = iterator.next();
				double tfIdfValue = map.get(value).doubleValue();
				double pCondition = 0;
				
				pCondition = Math.log(1/(Math.sqrt(2*Math.PI*variancePerWord[i][value])))
						-((tfIdfValue-averagePerWord[i][value])*(tfIdfValue-averagePerWord[i][value])
						/(2*variancePerWord[i][value]));
				
				probClassify[i] = probClassify[i]+pCondition;
			}
			probClassify[i]=probClassify[i]+Math.log(arrayPriorProbability.get(i).doubleValue());
		}
		int max = 0;
		for(int i = 0;i<totalTheme;i++){
			if(probClassify[max]<probClassify[i])
				max=i;
		}
		return max;
	}
}









