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
        String seg1 = "#>========================================\n" +
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

        String seg2 = "- segment: \n" +
                "      name: '\"__test__baidunews\"'\n" +
                "      args:  {}\n" +
                "      package: '\"tom\"'\n" +
                "      body: \n" +
                "         - load: '\"http://news.baidu.com/\"'";


        String seg3 = "- segment:\n" +
                "    name: '\"batchSpiderBloomingdales\"'\n" +
                "    args: {0: '/url', 1: '/operatorId'}\n" +
                "    body:\n" +
                "      - copy: {path: '/R/operId',value: '$/operatorId'}\n" +
                "      - load: '$/url'\n" +
                "      - js: '\"window.scrollTo(0,500)\"'\n" +
                "      - find: {ele: '#CCPA_banner button.close-small'}\n" +
                "      - if: '$1'\n" +
                "      - then:\n" +
                "        - click: {ele: '#CCPA_banner button.close-small',js: '$true'}\n" +
                "        - wait: {type: '\"time\"',millis: '2000'}\n" +
                "      - select: {eles: 'ul.items>li>div>a',path: '/R/producturls',attr: '\"href\"'}\n" +
                "      - loop:\n" +
                "          in: {start: '1',end: '10',step: '1'}\n" +
                "          each:\n" +
                "            - find: {ele: '#filterResultsTop li.nextArrow>a.action-btn'}\n" +
                "            - if: '$1'\n" +
                "            - then:\n" +
                "              - click: {ele: '#filterResultsTop li.nextArrow>a.action-btn'}\n" +
                "              - wait: {type: '\"time\"',millis: '3000'}\n" +
                "              - loop:\n" +
                "                  in: {eles: 'ul.items>li>div>a'}\n" +
                "                  each:\n" +
                "                    - select: {ele: '$e',attr: '\"href\"',path: '/tmpurl'}\n" +
                "                    - put: {path: '/R/producturls[-1]',value: '$/tmpurl'}\n" +
                "            - else:\n" +
                "              - break:\n" +
                "      - return: '$/R'";

        String seg4 = "- segment:\n" +
                "    package: '\"tf8\"'\n" +
                "    name: '\"alimama_campaign_apply\"'\n" +
                "    args: { 0: '/url', 1: '/campaignId'}\n" +
                "    body:\n" +
                "        - load: '$/url'\n" +
                "        - put: { path: '/ret/taskStatus', value: '1'}\n" +
                "        - put: { path: '/ret/campaignId', value: '$/campaignId'}\n" +
                "        - try:\n" +
                "            do:\n" +
                "                - find: { ele: '.login-panel'}\n" +
                "                - if: '$1'\n" +
                "                - then:\n" +
                "                    - callin: { package: '\"tf8\"', seg: '\"_alimama_apply_login\"' }\n" +
                "                - wait: {type: '\"visibility\"', ele: '.next-btn-primary'}\n" +
                "                - wait: {type: '\"time\"', millis: '1000'}\n" +
                "                - select: {ele: '.next-btn-primary', path: '/title' }\n" +
                "                - if: '$/title == \"申请计划\"'\n" +
                "                - then:\n" +
                "                    - safeclick: {ele: '.next-btn-primary'}\n" +
                "                    - safeclick: {ele: 'button.next-dialog-btn:nth-child(1)'}\n" +
                "            catch:\n" +
                "                - put: { path: '/ret/taskStatus', value: '0'}\n" +
                "        - echo: '$/ret'\n" +
                "        - return: '$/ret'";

        String seg5 = "- segment:\n" +
                "    package: '\"tf8\"'\n" +
                "    name: '\"alimama_campaign_apply\"'\n" +
                "    args: { 0: '/url', 1: '/campaignId'}\n" +
                "    body:\n" +
                "        - try:\n" +
                "            do:\n" +
                "                - echo: '\"1\"'\n" +
                "            catch:\n" +
                "                - echo: '\"2\"'\n" +
                "        - return: '$/ret'\n" +
                "        ";
        List<Segment> segmentList = SToken.getSegmentList(seg4);
        for (Segment segment:segmentList){
            System.out.println(segment.getPureCode());
        }

    }
}
