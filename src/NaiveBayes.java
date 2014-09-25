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
	
	double[][] averagePerWord;
	double[][] variancePerWord;
	boolean isUsedGaussian;
	
	
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
		isUsedGaussian = true;
	}
	
	public void setNumBoardMap(){
		numBoardMap.put(0, "Basketball");
	}
	
	public void setNumPerTheme(int sum){
		numPostEveryTheme.add(sum);
	}
	
	public void setTotalPost(int countPost){
		this.totalPost = countPost;
	}
	
	public void priorProbability(int i){		//计算先验概率
		double d = numPostEveryTheme.get(i).doubleValue()/totalPost;
		arrayPriorProbability.add(d);
		return ;
	}
	
	void perWordProbability(double[][] tfidfMatrix,int sumWord,int i){		//计算每个词的权重
		
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
	
	public void discreteContinuousAttribute(){
		
	}
	
	public void gaussianDistribution(double[][] tfidfMatrix){
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
	
	public int classify(Map<Integer, Double> map){
		double[] probClassify = new double[totalTheme];
		for(int i = 0;i<totalTheme;i++){
			probClassify[i] = 0;
		}
		if(isUsedGaussian){
			for(int i=0;i<totalTheme;i++){
				Set<Integer> set = map.keySet();
				Iterator<Integer> iterator = set.iterator();
				while(iterator.hasNext()){
					int value = iterator.next().intValue();
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
		return 1;
	}
}









