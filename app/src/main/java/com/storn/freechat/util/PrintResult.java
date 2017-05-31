package com.storn.freechat.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianshutong on 2017/3/13.
 */

public class PrintResult {

    public static void printString(String s) {
        String[] splitArray = s.split("，");
        List<String> resultArray = new ArrayList<>();

        for (int i = 0; i < splitArray.length; i++) {
            String preS = splitArray[i];
            String postS = splitArray[i + 1];
            String head = "";
            if (preS.length() == 2) {
                head = preS.substring(0);
            }
            resultArray.add(preS);
            if (postS.substring(0).equals(head)) {
                resultArray.add(postS);
            }
            System.out.println(head + "-->" + resultArray.toString());
        }

    }
}
