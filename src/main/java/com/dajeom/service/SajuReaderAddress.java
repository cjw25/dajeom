package com.dajeom.service;

import java.util.regex.Pattern;

/** Keeps the reader's chosen full name intact across generated passages. */
public final class SajuReaderAddress {
    private SajuReaderAddress() {}

    public static String of(String name) {
        String value=name.strip();
        return value.endsWith("님") ? value : value+"님";
    }

    public static String normalize(String text,String name) {
        String address=of(name);
        String base=address.substring(0,address.length()-1);
        String result=text.replace("[[상담자호칭]]",address);
        result=result.replaceAll("(?<![\\p{L}\\p{N}_])"+Pattern.quote(base)+"\\s*님(?:\\s*님)*",
            java.util.regex.Matcher.quoteReplacement(address));
        // Correct an abbreviated address only at a word boundary, never inside another person's name.
        if(base.matches("[가-힣]{3,5}")) {
            for(int offset=1;offset<=base.length()-2;offset++) {
                String shortName=base.substring(offset);
                result=result.replaceAll("(?<![\\p{L}\\p{N}_])"+Pattern.quote(shortName)+"\\s*님",
                    java.util.regex.Matcher.quoteReplacement(address));
            }
        }
        return result;
    }
}
