package kr.syeyoung.dungeonsguide.roomedit.valueedit;

import kr.syeyoung.dungeonsguide.roomedit.MPanel;
import kr.syeyoung.dungeonsguide.roomedit.Parameter;
import kr.syeyoung.dungeonsguide.roomedit.elements.MLabel;
import kr.syeyoung.dungeonsguide.roomedit.elements.MLabelAndElement;
import kr.syeyoung.dungeonsguide.roomedit.elements.MTextField;

import java.awt.*;

public class ValueEditString extends MPanel implements ValueEdit<String> {
    private Parameter parameter;


    public ValueEditString(Parameter parameter2) {
        this.parameter = parameter2;
        {
            MLabel label = new MLabel() {
                @Override
                public String getText() {
                    return parameter.getPreviousData().toString();
                }
            };
            MLabelAndElement mLabelAndElement = new MLabelAndElement("Prev",label);
            mLabelAndElement.setBounds(new Rectangle(0,0,bounds.width,20));
            add(mLabelAndElement);
        }
        {
            MTextField textField = new MTextField() {
                @Override
                public void edit(String str) {
                    parameter.setNewData(str);
                }
            };
            MLabelAndElement mLabelAndElement = new MLabelAndElement("New",textField);
            mLabelAndElement.setBounds(new Rectangle(0,20,bounds.width,20));
            add(mLabelAndElement);
        }
    }

    @Override
    public void onBoundsUpdate() {
        for (MPanel panel :getChildComponents()){
            panel.setSize(new Dimension(bounds.width, 20));
        }
    }

    @Override
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void resize(int parentWidth, int parentHeight) {
        this.setBounds(new Rectangle(0,0,parentWidth, parentHeight));
    }

    public static class Generator implements ValueEditCreator<ValueEditString> {

        @Override
        public ValueEditString createValueEdit(Parameter parameter) {
            return new ValueEditString(parameter);
        }

        @Override
        public Object createDefaultValue(Parameter parameter) {
            return "default";
        }

        @Override
        public Object cloneObj(Object object) {
            return object;
        }
    }
}
