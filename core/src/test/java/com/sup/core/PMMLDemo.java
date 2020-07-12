package com.sup.core;

/**
 * gongshuai
 * <p>
 * 2020/5/11
 */

import org.dmg.pmml.FieldName;

import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
class PMMLDemo {
    private Evaluator loadPmml(String path) {
        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream == null) {
            return null;
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
        Evaluator evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
        pmml = null;
        return evaluator;
    }

    private double predict(Evaluator evaluator, Map<String, Double> featuremap) {

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
                    return -9999.0f;
                }
            }
        }
        return  -9999.0f;


    }

    public static void main(String args[]) {
        PMMLDemo demo = new PMMLDemo();
        Evaluator model = demo.loadPmml("/Users/gongshuai/Downloads/A001.pmml");
        Map<String, Double> data = new HashMap<String, Double>();
        data.put("app2", 5.1);
        data.put("app3", 5.1);
        data.put("app4", 5.1);
        data.put("app5", 5.1);
        data.put("app6", 5.1);
        data.put("app7", 5.1);
        data.put("app8", 5.1);
        data.put("no_of_contract", 5.1);
        double  negprob =demo.predict(model, data);
        System.out.println(negprob);

    }
}

