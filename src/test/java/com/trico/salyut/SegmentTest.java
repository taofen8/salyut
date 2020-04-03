package com.trico.salyut;

import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.token.SToken;
import com.trico.salyut.token.Segment;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

public class SegmentTest extends TestCase  {
    public static Test suite()
    {
        return new TestSuite( SegmentTest.class );
    }

    public static void testSegment() throws SalyutException {
        String seg = "#>========================================\n" +
                "#> ps4game\n" +
                "#> main seg: ps4game\n" +
                "#> test seg: __test__ps4game\n" +
                "#> author: sheny\n" +
                "#> modify time: 2020-01-13\n" +
                "#>========================================\n" +
                "\n" +
                "- segment:\n" +
                "    name: '\"ps4game\"'\n" +
                "    package: '\"abs\"'\n" +
                "    args: { 0: '/url'}\n" +
                "    body:\n" +
                "      - load: '$/url'\n" +
                "      - echo: '$1'\n" +
                "      - loop:\n" +
                "          in: { eles: '.PF_1'}\n" +
                "          each:\n" +
                "            - select: { ele: '.tit a', path: '/tmp/title', under: '$e'}\n" +
                "            - select: { ele: 'img', path: '/tmp/img', under: '$e', attr: 'src'}\n" +
                "            - select: { ele: 'div:nth-child(3)', path: '/tmp/date', under: '$e' ,regex: '\"([\\d|\\-]+)\"'}\n" +
                "            - select: { ele: 'div:nth-child(4)', path: '/tmp/type', under: '$e' ,regex: '\"(?<=游戏类型：)(.*)\"'}\n" +
                "            - select: { ele: 'div:nth-child(5)', path: '/tmp/company', under: '$e' ,regex: '\"(?<=制作发行：)(.*)\"'}\n" +
                "            - select: { ele: '.Intr p', path: '/tmp/desc', under: '$e' }\n" +
                "            - copy: { path: '/items[-1]', value: '$/tmp'}\n" +
                "      - return: '$/items'\n" +
                "\n" +
                "- segment:\n" +
                "    name: '\"__test__ps4game\"'\n" +
                "    args: {}\n" +
                "    body:\n" +
                "      - callin: { seg: '\"ps4game\"', 0: '\"https://ku.gamersky.com/release/ps4_201912/\"'}\n" +
                "      - if: '$1[0]/title == \"祖先：人类史诗\"'\n" +
                "      - then:\n" +
                "        - return: '$true'\n" +
                "      - else:\n" +
                "        - return: '$false'\n" +
                "\n" +
                "- callin: {seg: '\"__test__ps4game\"', 0: '\"1\"'}\n" +
                "- echo: '$1'\n" +
                "# - callin: { seg: '\"ps4game\"', 0: '\"https://ku.gamersky.com/release/ps4_201912/\"'}\n" +
                "# - echo: '$1'";

        List<Segment> segmentList = SToken.getSegmentList(seg);
        for (Segment segment:segmentList){
            System.out.println(segment.getPureCode());
        }

    }
}
