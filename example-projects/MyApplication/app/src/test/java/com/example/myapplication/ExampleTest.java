package com.example.myapplication;

import org.junit.Assert;
import org.junit.Test;

public class ExampleTest {
   @Test
   public void test_bar_1() {
      System.out.println("test_bar_1");
      Assert.assertEquals(83L, (long)(new Example()).bar(93, false));
   }

   @Test
   public void test_bar_10() {
      System.out.println("test_bar_10");
      Assert.assertEquals(-38L, (long)(new Example()).bar(-28, false));
   }

   @Test
   public void test_bar_2() {
      System.out.println("test_bar_2");
      Assert.assertEquals(72L, (long)(new Example()).bar(82, false));
   }

   @Test
   public void test_bar_3() {
      System.out.println("test_bar_3");
      Assert.assertEquals(-109L, (long)(new Example()).bar(-99, false));
   }

   @Test
   public void test_bar_4() {
      System.out.println("test_bar_4");
      Assert.assertEquals(-34L, (long)(new Example()).bar(-24, false));
   }

   @Test
   public void test_bar_5() {
      System.out.println("test_bar_5");
      Assert.assertEquals(81L, (long)(new Example()).bar(71, true));
   }

   @Test
   public void test_bar_6() {
      System.out.println("test_bar_6");
      Assert.assertEquals(88L, (long)(new Example()).bar(78, true));
   }

   @Test
   public void test_bar_7() {
      System.out.println("test_bar_7");
      Assert.assertEquals(26L, (long)(new Example()).bar(36, false));
   }

   @Test
   public void test_bar_8() {
      System.out.println("test_bar_8");
      Assert.assertEquals(83L, (long)(new Example()).bar(93, false));
   }

   @Test
   public void test_bar_9() {
      System.out.println("test_bar_9");
      Assert.assertEquals(65L, (long)(new Example()).bar(55, true));
   }

   @Test
   public void test_foo_1() {
      System.out.println("test_foo_1");
      (new Example()).foo(new Callee("QHqDssGzB8zYVDlWY2UYyof4c5JJmdDBvD"));
   }

   @Test
   public void test_foo_10() {
      System.out.println("test_foo_10");
      (new Example()).foo(new Callee("OncZ0noyqMXxMldbi6J7h3PKmhXBc6D3qxPw9EoJYp-nos7z-NKCx-nXF+L6YiRCqvYXkL6"));
   }

   @Test
   public void test_foo_2() {
      System.out.println("test_foo_2");
      (new Example()).foo(new Callee("pyVFkPzB9L9MwpTee4JnYAQ5JrcyXKrMRMVP"));
   }

   @Test
   public void test_foo_3() {
      System.out.println("test_foo_3");
      (new Example()).foo(new Callee("aslrqrjODfTjhwzlYEkgGtY67n5+q1g1eLGwR"));
   }

   @Test
   public void test_foo_4() {
      System.out.println("test_foo_4");
      (new Example()).foo(new Callee("6dpWhLs0JIgs9f9n-LkXmwZeJ2wzBCfK8iS51T"));
   }

   @Test
   public void test_foo_5() {
      System.out.println("test_foo_5");
      (new Example()).foo(new Callee("2tPTt0pfsYf"));
   }

   @Test
   public void test_foo_6() {
      System.out.println("test_foo_6");
      (new Example()).foo(new Callee("i8svgR6Dsu6PtB+Ga0oPO5wp8eCfalRgoDFC5Dm6VdD8l0UFxqDpn6cwfQwTNO"));
   }

   @Test
   public void test_foo_7() {
      System.out.println("test_foo_7");
      (new Example()).foo(new Callee("Sf"));
   }

   @Test
   public void test_foo_8() {
      System.out.println("test_foo_8");
      (new Example()).foo(new Callee("R-AjANOnnZT2pn+Oy7C"));
   }

   @Test
   public void test_foo_9() {
      System.out.println("test_foo_9");
      (new Example()).foo(new Callee("HVMvnz5hMhjH6VDaSwkzRdX3or"));
   }

   @Test
   public void test_getTriangleType_1() {
      System.out.println("test_getTriangleType_1");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(-56.96739027286009, -6.701292045995729, -34.40928635606912));
   }

   @Test
   public void test_getTriangleType_10() {
      System.out.println("test_getTriangleType_10");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(14.377833997587032, 54.344278167281686, -13.237167665336074));
   }

   @Test
   public void test_getTriangleType_2() {
      System.out.println("test_getTriangleType_2");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(54.87941801734172, 53.86344354528265, -93.64639609054788));
   }

   @Test
   public void test_getTriangleType_3() {
      System.out.println("test_getTriangleType_3");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(-82.92415777347881, 14.442732634229799, 70.3007272762232));
   }

   @Test
   public void test_getTriangleType_4() {
      System.out.println("test_getTriangleType_4");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(35.583939317589, -88.15052108953849, -75.93927599008134));
   }

   @Test
   public void test_getTriangleType_5() {
      System.out.println("test_getTriangleType_5");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(-9.407201245495827, -11.635545199191725, -8.28853398993823));
   }

   @Test
   public void test_getTriangleType_6() {
      System.out.println("test_getTriangleType_6");
      Assert.assertEquals("Invalid triangle: type2", (new Example()).getTriangleType(96.47966099552514, 37.7041077418489, 19.22454084819971));
   }

   @Test
   public void test_getTriangleType_7() {
      System.out.println("test_getTriangleType_7");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(-60.51842415268149, 17.557828513307626, 99.89138826473442));
   }

   @Test
   public void test_getTriangleType_8() {
      System.out.println("test_getTriangleType_8");
      Assert.assertEquals("Invalid triangle: type2", (new Example()).getTriangleType(49.67099134627398, 2.611974317559657, 1.9968145259030763));
   }

   @Test
   public void test_getTriangleType_9() {
      System.out.println("test_getTriangleType_9");
      Assert.assertEquals("Invalid triangle: type 1", (new Example()).getTriangleType(-20.64933414200823, -9.433661994949418, 50.186671479899985));
   }

   @Test
   public void test_isValidChemicalId_1() {
      System.out.println("test_isValidChemicalId_1");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("DXY-kcm1YgHlZp2KJSJ8M"));
   }

   @Test
   public void test_isValidChemicalId_10() {
      System.out.println("test_isValidChemicalId_10");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("Mj-c+MGllODNjSsjGzHI4AnyFcWP1uI9kzM5Zm0gmJql3Eb-H1TYiFrkUT88C"));
   }

   @Test
   public void test_isValidChemicalId_2() {
      System.out.println("test_isValidChemicalId_2");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("dbCXzPud6aBqDBEayYx031a6HrOB3058yhXi0hCvngKXigEZp3BydJKn3+MkGOlBExU"));
   }

   @Test
   public void test_isValidChemicalId_3() {
      System.out.println("test_isValidChemicalId_3");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("GaHySKdKGm+bhbMZXMqh0MO91gPOOmOnCDwLP3m5YezAdAiF2EGAZcgOoIINaAbrDkgU"));
   }

   @Test
   public void test_isValidChemicalId_4() {
      System.out.println("test_isValidChemicalId_4");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("sBfLz6TVV9E"));
   }

   @Test
   public void test_isValidChemicalId_5() {
      System.out.println("test_isValidChemicalId_5");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("dMwo6IAJ0LgLnSLvXbGyuk0Nr1BaCjHC1imW+g6R7Y54398ijJzDg1kQCiH1Z+BJCVj2DjRcOzk3"));
   }

   @Test
   public void test_isValidChemicalId_6() {
      System.out.println("test_isValidChemicalId_6");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("X"));
   }

   @Test
   public void test_isValidChemicalId_7() {
      System.out.println("test_isValidChemicalId_7");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("FtQ0oODFK-IVDdUit7LisnZLGxnlHE+tz7Mq-kic26sxiKRv"));
   }

   @Test
   public void test_isValidChemicalId_8() {
      System.out.println("test_isValidChemicalId_8");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("kks1XXtGJoLiQn2202EbsGVKpiUyvul54fyr-OyJvPwIA+hLJE2JgCimJJq+f9ohtdP"));
   }

   @Test
   public void test_isValidChemicalId_9() {
      System.out.println("test_isValidChemicalId_9");
      Assert.assertEquals(false, (new Example()).isValidChemicalId("bPW3N75XzwXn5fX70t61Cca9cXmLft3W"));
   }

   @Test
   public void test_isValidISBN_1() {
      System.out.println("test_isValidISBN_1");
      Assert.assertEquals(false, (new Example()).isValidISBN("kN2g-0kq8+bIKMRKpXNr6UaMcEpZ1VeYGCcaR9ktqFFr3C15Acx0GaCTVn6a1u6pKbihvx"));
   }

   @Test
   public void test_isValidISBN_10() {
      System.out.println("test_isValidISBN_10");
      Assert.assertEquals(false, (new Example()).isValidISBN("+lXtUxtNJJdGkEv3lttlYXZAdOXaOkjL+46qRQ0SGKWso5xIj9OPJShd8EDv3TwlMj"));
   }

   @Test
   public void test_isValidISBN_2() {
      System.out.println("test_isValidISBN_2");
      Assert.assertEquals(false, (new Example()).isValidISBN("CKnKlAg7l7eWRIrHbOggIRr5mrR2c9GN8FHerJUFImjUpd+xfoKo+"));
   }

   @Test
   public void test_isValidISBN_3() {
      System.out.println("test_isValidISBN_3");
      Assert.assertEquals(false, (new Example()).isValidISBN("HbF03DSGz20QQYfCRMM3QpHaQuPHkllaXAkyslFDMY7UGfIaPkm4MWL0qU2u"));
   }

   @Test
   public void test_isValidISBN_4() {
      System.out.println("test_isValidISBN_4");
      Assert.assertEquals(false, (new Example()).isValidISBN("wt6V805F4FnDaM9xtrUgY9rVXYb35SMayJDIRpEeXuaMnjZrxWptca6ZUxeaH1UbSJSN1w+hsV"));
   }

   @Test
   public void test_isValidISBN_5() {
      System.out.println("test_isValidISBN_5");
      Assert.assertEquals(false, (new Example()).isValidISBN("7gwyfDywhqGO"));
   }

   @Test
   public void test_isValidISBN_6() {
      System.out.println("test_isValidISBN_6");
      Assert.assertEquals(false, (new Example()).isValidISBN("zmHdwTYxtomBMX3lf8Z2VQkdsWShTXr-nPxw1uoLVCtQXBc+TOZR0N9pwhX4GE+FPzhS5XqJnGQZNQy32bktF-GF2"));
   }

   @Test
   public void test_isValidISBN_7() {
      System.out.println("test_isValidISBN_7");
      Assert.assertEquals(false, (new Example()).isValidISBN("WbD6y1NaMQuKldfqSF3hWVjPq8gTGkwRBqAkNGHWtCHhyrPouHiNl2XTELV"));
   }

   @Test
   public void test_isValidISBN_8() {
      System.out.println("test_isValidISBN_8");
      Assert.assertEquals(false, (new Example()).isValidISBN("OqtpxEIC-aTJfPEoFId9hMtOiacHUqh95fhdvCh"));
   }

   @Test
   public void test_isValidISBN_9() {
      System.out.println("test_isValidISBN_9");
      Assert.assertEquals(false, (new Example()).isValidISBN("pz5tQSYLugXSPBlFdgdcuCzBhUxy0zyZCo2FnNY8vojk9-4lnm-fzlH4-HIjdEn+6"));
   }
}
