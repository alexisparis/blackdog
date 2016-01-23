package org.siberia.utilities.math;

import java.text.ParseException;
import java.util.List;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class MatrixTest extends TestCase
{
    public MatrixTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MatrixTest.class);
        
        return suite;
    }
    
    public void testConstructors() throws Exception
    {
	Matrix m = null;
	try {   m = new Matrix(0);assertTrue(false); } catch(IllegalArgumentException e){ assertTrue(true); }
	try {   m = new Matrix(0, 0);assertTrue(false); } catch(IllegalArgumentException e){ assertTrue(true); }
	try {   m = new Matrix(-1, -5);assertTrue(false); } catch(IllegalArgumentException e){ assertTrue(true); }
	try {   m = new Matrix(-1, 14);assertTrue(false); } catch(IllegalArgumentException e){ assertTrue(true); }
	try {   m = new Matrix(1, 0);assertTrue(false); } catch(IllegalArgumentException e){ assertTrue(true); }
	try {   m = new Matrix(null);assertTrue(false); } catch(NullPointerException e){ assertTrue(true); }
	
	m = new Matrix(1);
	assertEquals(1, m.getRowDimension());
	assertEquals(1, m.getColumnDimension());
	m = new Matrix(10);
	assertEquals(10, m.getRowDimension());
	assertEquals(10, m.getColumnDimension());
	m = new Matrix(10, 10);
	assertEquals(10, m.getRowDimension());
	assertEquals(10, m.getColumnDimension());
	m = new Matrix(10, 30);
	assertEquals(10, m.getRowDimension());
	assertEquals(30, m.getColumnDimension());
	m = new Matrix(10, 30, 0);
	assertEquals(10, m.getRowDimension());
	assertEquals(30, m.getColumnDimension());
	m = new Matrix(10, 30, -1);
	assertEquals(10, m.getRowDimension());
	assertEquals(30, m.getColumnDimension());
    }
    
    public void testGetSet() throws Exception
    {
	Matrix m = null;
	Number n = null;
	
	m = new Matrix(4, 5);
	
	n = 3.6;
	m.set(2, 3, n);
	assertEquals(n, m.get(2, 3));
	
	n = 5.14;
	m.set(0, 4, n);
	assertEquals(n, m.get(0, 4));
    }
    
    public void testFill() throws Exception
    {
	Matrix m = null;
	
	m = new Matrix(3, 2);
	
	for(int i = 0; i < m.getRowDimension(); i++)
	{
	    for(int j = 0; j < m.getColumnDimension(); j++)
	    {
		assertEquals(0, m.get(i, j));
	    }
	}
	
	m.fill(2.3);
	
	for(int i = 0; i < m.getRowDimension(); i++)
	{
	    for(int j = 0; j < m.getColumnDimension(); j++)
	    {
		assertEquals(2.3, m.get(i, j));
	    }
	}
	
	m.fillRow(1, 6.52);
	
	for(int i = 0; i < m.getRowDimension(); i++)
	{
	    for(int j = 0; j < m.getColumnDimension(); j++)
	    {
		if ( i == 1 )
		{
		    assertEquals(6.52, m.get(i, j));
		}
		else
		{
		    assertEquals(2.3, m.get(i, j));
		}
	    }	
	}
	
	m.fill(6.52);
	m.fillColumn(0, 5.0);
	
	for(int i = 0; i < m.getRowDimension(); i++)
	{
	    for(int j = 0; j < m.getColumnDimension(); j++)
	    {
		if ( j == 0 )
		{
		    assertEquals(5.0, m.get(i, j));
		}
		else
		{
		    assertEquals(6.52, m.get(i, j));
		}
	    }	
	}
    }
    
    public void testExtraction() throws Exception
    {
	Matrix m = null;
	Matrix extracted = null;
	
	m = Matrix.createMatrix("[[ 1,  2,  3]" +
				 "[ 4,  5,  6]" +
				 "[ 7,  8,  9]" +
				 "[10, 11, 12]]");
	assertEquals(4, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	
	try
	{   extracted = m.createExtractedMatrix(0, 4, 0, 2);
	    assertTrue(false);
	}
	catch(IllegalArgumentException e)
	{   assertTrue(true); }
	try
	{   extracted = m.createExtractedMatrix(0, 3, 0, 3);
	    assertTrue(false);
	}
	catch(IllegalArgumentException e)
	{   assertTrue(true); }
	try
	{   extracted = m.createExtractedMatrix(4, 6, 0, 3);
	    assertTrue(false);
	}
	catch(IllegalArgumentException e)
	{   assertTrue(true); }
	try
	{   extracted = m.createExtractedMatrix(0, 3, 4, 9);
	    assertTrue(false);
	}
	catch(IllegalArgumentException e)
	{   assertTrue(true); }
	try
	{   extracted = m.createExtractedMatrix(3, 2, 0, 3);
	    assertTrue(false);
	}
	catch(IllegalArgumentException e)
	{   assertTrue(true); }
	try
	{   extracted = m.createExtractedMatrix(0, 3, 2, 1);
	    assertTrue(false);
	}
	catch(IllegalArgumentException e)
	{   assertTrue(true); }
	
	extracted = m.createExtractedMatrix(0, 3, 0, 2);
	assertEquals(4, extracted.getRowDimension());
	assertEquals(3, extracted.getColumnDimension());
	assertEquals(m.get(0, 0), extracted.get(0, 0));
	
	extracted = m.createExtractedMatrix(0, 1, 1, 2);
	assertEquals(2, extracted.getRowDimension());
	assertEquals(2, extracted.getColumnDimension());
	assertEquals(m.get(0, 1), extracted.get(0, 0));
	
	extracted = m.createRowMatrix(2);
	assertEquals(1, extracted.getRowDimension());
	assertEquals(m.getColumnDimension(), extracted.getColumnDimension());
	assertEquals(m.get(2, 0), extracted.get(0, 0));
	
	extracted = m.createColumnMatrix(1);
	assertEquals(m.getRowDimension(), extracted.getRowDimension());
	assertEquals(1, extracted.getColumnDimension());
	assertEquals(m.get(0, 1), extracted.get(0, 0));
    }
    
    public void testOperations() throws Exception
    {
	Matrix m1        = null;
	Matrix m2        = null;
	Matrix processed = null;
	Matrix result    = null;
	
	m1 = Matrix.createMatrix("[[ 1,  2,  3]" +
				  "[ 4,  5,  6]" +
				  "[ 7,  8,  9]" +
				  "[10, 11, 12]" +
				  "[13, 14, 15]]");
	
	m2 = Matrix.createMatrix("[[ 4,  6,    8]" +
				  "[10,  1,    2]" +
				  "[ 4, -5, -6.3]" +
				  "[ 5,  6,   -8]" +
				  "[ 1,  0,    0]]");
	
	result = Matrix.createMatrix("[[5, 8, 11]" +
				      "[14, 6, 8]" +
				      "[11, 3, 2.7]" +
				      "[15, 17, 4]" +
				      "[14, 14, 15]]");
	
	processed = m1.plus(m2);
	
	assertTrue(result.equals(processed));
	processed = m2.plus(m1);
	assertTrue(result.equals(processed));
	
	result = Matrix.createMatrix("[[-3, -4, -5]" +
				      "[-6, 4, 4]" +
				      "[3, 13, 15.3]" +
				      "[5, 5, 20]" +
				      "[12, 14, 15]]");
	
	processed = m1.minus(m2);
	assertTrue(result.equals(processed));
	
	result = Matrix.createMatrix("[[1, 4, 7, 10, 13]" +
				      "[2, 5, 8, 11, 14]" +
				      "[3, 6, 9, 12, 15]]");
	processed = m1.transpose();
	assertTrue(result.equals(processed));
	
	result = Matrix.createMatrix("[[4, 10, 4, 5, 1]" +
				      "[6, 1, -5, 6, 0]" +
				      "[8, 2, -6.3, -8, 0]]");
	processed = m2.transpose();
	assertTrue(result.equals(processed));
	
	result = Matrix.createMatrix("[[ 4,  8,  12]" +
		         	      "[ 16,  20,  24]" +
				      "[ 28,  32,  36]" +
				      "[40, 44, 48]" +
				      "[52, 56, 60]]");
	processed = m1.multiply(4);
	assertTrue(result.equals(processed));
	
	
	
	m2 = Matrix.createMatrix(	    "[[   4,    6,    8,    5,   6]" +
					     "[  10,    1,    2,   -1,   2]" +
					     "[   4,   -5,   -1,    0,   0]]");
	
	m1 = Matrix.createMatrix("[[ 1,  2,  3]" +
				  "[ 4,  5,  6]" +
				  "[ 7,  8,  9]" +
				  "[10, 11, 12]" +
				  "[13, 14, 15]]");
	
	result = Matrix.createMatrix(        "[[36 , -7  ,  9  ,  3   ,   10]" +
					      "[ 90, -1  , 36  ,   15 ,  34 ]" +
					      "[144,  5  ,  63 ,   27 ,  58 ]" + 
					      "[198, 11  , 90  ,  39  , 82  ]" + 
					      "[252, 17  , 117 , 51   , 106]]");
	processed = m1.multiply(m2);
	
	
	assertTrue(result.equals(processed));
    }
    
    public void testIdentity() throws Exception
    {
	Matrix m = null;
	
	m = Matrix.createMatrix("[[ 1,  2,  3]" +
				 "[ 4,  5,  6]" +
				 "[ 7,  8,  9]" +
				 "[10, 11, 12]]");
	
	assertFalse(m.isIdentity());
	
	m = Matrix.createIdentityMatrix(4);
	assertTrue(m.isIdentity());
	
	m.set(2, 1, 1.3);
	assertFalse(m.isIdentity());
    }
    
    public void testCreateMatrix() throws Exception
    {
	Matrix m = Matrix.createMatrix("[[1, 2, 3][4, 5, 6][7, 8, 9]]");
	assertEquals(3, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertEquals(1.0, m.get(0, 0));
	assertEquals(2.0, m.get(0, 1));
	assertEquals(3.0, m.get(0, 2));
	assertEquals(4.0, m.get(1, 0));
	assertEquals(5.0, m.get(1, 1));
	assertEquals(6.0, m.get(1, 2));
	assertEquals(7.0, m.get(2, 0));
	assertEquals(8.0, m.get(2, 1));
	assertEquals(9.0, m.get(2, 2));
	
	m = Matrix.createMatrix("[[1, 2, 3][4, 5, 6]]");
	assertEquals(2, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertEquals(1.0, m.get(0, 0));
	assertEquals(2.0, m.get(0, 1));
	assertEquals(3.0, m.get(0, 2));
	assertEquals(4.0, m.get(1, 0));
	assertEquals(5.0, m.get(1, 1));
	assertEquals(6.0, m.get(1, 2));
	
	m = Matrix.createMatrix("[[1.236, 2, 35454][4, 5.2, 6]]");
	assertEquals(2, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertEquals(1.236, m.get(0, 0));
	assertEquals(2.0, m.get(0, 1));
	assertEquals(35454.0, m.get(0, 2));
	assertEquals(4.0, m.get(1, 0));
	assertEquals(5.2, m.get(1, 1));
	assertEquals(6.0, m.get(1, 2));
	
	/** rows of different size */
	try {   m = Matrix.createMatrix("[[1.236, 2, 35454][4, 5.2, 6, 8]]");assertTrue(false); }
	catch(ParseException e)	{   assertTrue(true); }
	/** contains an empty row */
	try {   m = Matrix.createMatrix("[[][4, 5.2, 6, 8]]");assertTrue(false); }
	catch(ParseException e)	{   assertTrue(true); }
	/** contains empty rows */
	try {   m = Matrix.createMatrix("[[][]]");assertTrue(false); }
	catch(ParseException e)	{   assertTrue(true); }
	/** does not start by '[' */
	try {   m = Matrix.createMatrix("[1.236, 2, 35454][4, 5.2, 8]]");assertTrue(false); }
	catch(ParseException e)	{   assertTrue(true); }
	/** invalid number */
	try {   m = Matrix.createMatrix("[[1.A236, 2, 35454][4, 5.2, 8]]");assertTrue(false); }
	catch(NumberFormatException e)	{   assertTrue(true); }
    }
    
    
    
}
