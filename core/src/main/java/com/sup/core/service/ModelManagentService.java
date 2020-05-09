package com.sup.core.service;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
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
            return  -9999.0f;
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
        List<TargetField> targetFields = evaluator.getTargetFields();

        TargetField targetField = targetFields.get(0);
        FieldName targetFieldName = targetField.getName();

        Object targetFieldValue = results.get(targetFieldName);
        System.out.println("target: " + targetFieldName.getValue() + " value: " + targetFieldValue);
        double primitiveValue = -1.0f;
        if (targetFieldValue instanceof Computable) {
            Computable computable = (Computable) targetFieldValue;
            System.out.println(computable.getResult());

            primitiveValue = (double) computable.getResult();
        }
        return primitiveValue;
    }


}
