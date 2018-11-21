package lucene.file.search.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * Created by  huochao2  on 2018/11/16
 */
public class IKAnalyzer6x extends Analyzer {
    private boolean useSmart;
    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public IKAnalyzer6x() {
        this(false); //IK分词器Lucene Analyzer接口实现类;
        //默认细粒度切分算法
    }
    //IK分词器Lucene Analyzer接口实现类；当为True时，分词器进行智能切分
    public IKAnalyzer6x(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    //重写最新版本的createComponents;重载Analyzer接口，构造分词组件
    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fileName) {
        Tokenizer _IKTokenizer = new IKTokenizer6x(this.isUseSmart());
        return new Analyzer.TokenStreamComponents(_IKTokenizer);
    }
}
