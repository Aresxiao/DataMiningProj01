import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class NaiveBayes {
	
	ArrayList<Double> arrayPriorProbability;	//�������
	ArrayList<Integer> numPostEveryTheme;		//ÿ���������ӵ���Ŀ
	ArrayList<Integer> numWordsEveryTheme;		//ÿ������ʵĸ���
	int totalPost;							//�ܵ�������Ŀ
	double[][] perWordProbability;			//ÿ������ÿ�������ϵ���������
	int totalKeywords;						//�ܵĹؼ�����Ŀ
	int totalTheme;							//�ܵ�������Ŀ
	Map<Integer,String> numBoardMap;			//
	
	double[][] averagePerWord;
	double[][] variancePerWord;
	
	
	public NaiveBayes(int countPost,int countKeywords,int countTheme){
		arrayPriorProbability = new ArrayList<Double>();
		numPostEveryTheme = new ArrayList<Integer>();
		totalKeywords = countKeywords;
		totalPost = countPost;
		totalTheme = countTheme;
		perWordProbability = new double[totalTheme][totalKeywords];
		averagePerWord = new double[totalTheme][totalKeywords];
		variancePerWord = new double[totalTheme][totalKeywords];
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
	
	public void priorProbability(int i){		//�����������
		double d = numPostEveryTheme.get(i).doubleValue()/totalPost;
		arrayPriorProbability.add(d);
		return ;
	}
	
	void perWordProbability(double[][] tfidfMatrix,int sumWord,int i){		//����ÿ���ʵ�Ȩ��
		
	}
	
	double getCountWordsPerClass(int i){
		double sum=0;
		return sum;
	}
	public void setWordsMatrix(double[][] matrix){
		for(int i = 0;i < totalPost;i++){
			
			for(int j = 0;j < totalKeywords;j++){
				perWordProbability[i][j] = matrix[i][j];
			}
		}
	}
	
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
				perWordProbability[i][j] = perWordProbability[i][j]/sum;
		}
	}
	
	public void discreteContinuousAttributeNBCD(double[][] tfidfMatrix){
		Iterator<Integer> iterator = numPostEveryTheme.iterator();
		int flagRow = 0;
		int flagTheme = 0;
		while(iterator.hasNext()){
			int numPost = iterator.next().intValue();
			for(int j=0;j<totalKeywords;j++){
				for(int i=flagRow;i<(flagRow+numPost);i++){
					int discreteValue;
					if(tfidfMatrix[i][j]<1.5)
						discreteValue=1;
					else if(tfidfMatrix[i][j]<2.5){
						discreteValue=2;
					}
					else if(tfidfMatrix[i][j]<3.5)
						discreteValue=3;
					else if(tfidfMatrix[i][j]<4.5){
						discreteValue=4;
					}
					else {
						discreteValue=5;
					}
					perWordProbability[flagTheme][j] = perWordProbability[flagTheme][j]+discreteValue;
				}
				
			}
			flagRow = flagRow+numPost;
			flagTheme++;
		}
		for(int i = 0;i<totalTheme;i++){
			double sum = 0;
			for(int j = 0;j<totalKeywords;j++){
				sum = sum+perWordProbability[i][j];
			}
			for(int j = 0;j<totalKeywords;j++)
				perWordProbability[i][j] = (perWordProbability[i][j]+1)/(sum+totalKeywords);
		}
	}
	
	public void gaussianDistribution(double[][] tfidfMatrix){		//��˹�ֲ�
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
				variance = sum/numPost+0.00000001;
				
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
		}
		int max=0;
		for(int i=0;i<totalTheme;i++){
			if(probClassify[max]<probClassify[i])
				max=i;
		}
		return max;
	}
	
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
				probClassify[i] = probClassify[i]+Math.log(perWordProbability[i][column]);
			}
		}
		int max = 0;
		for(int i = 0;i<totalTheme;i++){
			if(probClassify[max]<probClassify[i])
				max=i;
		}
		return max;
	}
	
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
				double pCondition = Math.log(1/(Math.sqrt(2*Math.PI*variancePerWord[i][value])))
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









