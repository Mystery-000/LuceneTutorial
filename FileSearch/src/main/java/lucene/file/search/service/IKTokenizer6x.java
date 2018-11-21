package lucene.file.search.service;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;

/**
 * Created by  huochao2  on 2018/11/16
 */
public class IKTokenizer6x extends Tokenizer {
    //IK 分词器实现
    private IKSegmenter _IKImplement;
    // 词元文本属性
    private  final CharTermAttribute termAttribute;
    //词元位移属性
    private final OffsetAttribute offsetAttribute;
    private final TypeAttribute typeAttribute;
    //记录最后一个词元的结束位置
    private  int endPosition;
    //Lucene 6.0x Tokenizer 适配器构造函数;实现最新的Tokenizer接口
    public IKTokenizer6x(boolean useSmart) {
        super();
        offsetAttribute = addAttribute(OffsetAttribute.class);
        termAttribute = addAttribute(CharTermAttribute.class);
        typeAttribute = addAttribute(TypeAttribute.class);
        _IKImplement = new IKSegmenter(input, useSmart);
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes(); //清除所有词元属性
        Lexeme nextLexeme = _IKImplement.next();
        if(nextLexeme != null) {
            //将nextLexeme转成Attributes
            termAttribute.append(nextLexeme.getLexemeText()); //设置词元文本
            termAttribute.setLength(nextLexeme.getLength()); //设置词元长度
            offsetAttribute.setOffset(nextLexeme.getBeginPosition(),
                    nextLexeme.getEndPosition()); //设置词元位移
            //记录分词的最后位置
            endPosition = nextLexeme.getEndPosition();
            typeAttribute.setType(nextLexeme.getLexemeText()); //记录词元分类
            return  true; //返回true告知还有下一个词元
        }
        return  false; //返回false告知词元输出完毕
    }
    @Override
    public void reset() throws IOException {
        super.reset();
        _IKImplement.reset(input);
    }
    @Override
    public final void  end() {
        int finalOffset = correctOffset(this.endPosition);
        offsetAttribute.setOffset(finalOffset,finalOffset);
    }
}
