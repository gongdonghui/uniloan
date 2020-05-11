package com.sup.core.service;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * gongshuai
 * <p>
 * 2020/3/29
 */
@Service
public class ModelManagentService {
    @Value("${model.name}")
    private String modelName;
    @Value("${model.path}")
    private String modelPath;

    private  Evaluator  evaluator;

    private static final Double  DEFAULT_VALUE = -9999.0;

    @PostConstruct
    private void loadPmml() {
        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(this.modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream == null) {
            return ;
        }
        InputStream is = inputStream;
        try {
            pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (JAXBException e1) {
            e1.printStackTrace();
        } finally {
            //关闭输入流
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
        this.evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
        pmml = null;
    }

    public double predict( Map<String, Double> featuremap) {
        if(this.evaluator ==null) {
            return  DEFAULT_VALUE;
        }

        List<InputField> inputFields = evaluator.getInputFields();
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
        for (InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            Object rawValue = featuremap.get(inputFieldName.getValue());
            FieldValue inputFieldValue = inputField.prepare(rawValue);
            arguments.put(inputFieldName, inputFieldValue);
        }

        Map<FieldName, ?> results = evaluator.evaluate(arguments);

        for (FieldName fieldName : results.keySet()) {
            if (fieldName.toString().equals("probability(1)")) {
                Object ret = results.get(fieldName);
                if (ret instanceof Float || ret instanceof   Double) {
                    return (Float) ret;
                } else {
                    return  DEFAULT_VALUE;
                }
            }
        }
        return  DEFAULT_VALUE;
    }


}
