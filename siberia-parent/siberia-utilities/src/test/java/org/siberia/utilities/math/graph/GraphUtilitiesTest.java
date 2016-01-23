package org.siberia.utilities.math.graph;

import junit.framework.*;
import org.siberia.utilities.math.Matrix;

/**
 *
 * @author alexis
 */
public class GraphUtilitiesTest extends TestCase
{
    public GraphUtilitiesTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(GraphUtilitiesTest.class);
        
        return suite;
    }
    
    public void testContainsCycle() throws Exception
    {
	Matrix  m      = null;
	boolean result = true;
	
	result = true;
	m = Matrix.createMatrix("[[1]]");
	m.print();
	assertEquals(result, GraphUtilities.containsCycle(m));
	
	result = false;
	m = Matrix.createMatrix("[[0]]");
	assertEquals(result, GraphUtilities.containsCycle(m));
	
	result = true;
	//  1    2
	//  
	//   --->
	//   <---
	// cycle {1, 2}
	m = Matrix.createMatrix("[[0, 1]" +
				 "[1, 0]]");
	assertEquals(result, GraphUtilities.containsCycle(m));
	
	result = true;
	//  1    2
	//  
	//   <---
	// cycle {1, 1}
	m = Matrix.createMatrix("[[1, 0]" +
				 "[1, 0]]");
	assertEquals(result, GraphUtilities.containsCycle(m));
	
	result = false;
	//  1    2    3    4    5
	//  
	//   <--------
	//             <---
	//   <-------------
	//                  <---
	//             <--------
	//   <------------------
	//        <---
	//        <-------------
	m = Matrix.createMatrix("[[0, 0, 0, 0, 0]" +
				 "[0, 0, 0, 0, 0]" +
				 "[1, 1, 0, 0, 0]" +
				 "[1, 0, 1, 0, 0]" +
				 "[1, 1, 1, 1, 0]]");
	assertEquals(result, GraphUtilities.containsCycle(m));
	
	result = false;
	//  1    2    3    4    5
	//  
	//   <--------
	//             <---
	//   <-------------
	//                  <---
	//             <--------
	//   <------------------
	//        <---
	//        <-------------
	//   --->
	m = Matrix.createMatrix("[[0, 1, 0, 0, 0]" +
				 "[0, 0, 0, 0, 0]" +
				 "[1, 1, 0, 0, 0]" +
				 "[1, 0, 1, 0, 0]" +
				 "[1, 1, 1, 1, 0]]");
	assertEquals(result, GraphUtilities.containsCycle(m));
	
	result = true;
	//  1    2    3    4    5
	//  
	//   --->
	//	  --->
	//        -------->
	//   ------------------>
	//        <-------------
	//             <--------
	//                  --->
	// cycle {5, 2, 4}
	m = Matrix.createMatrix("[[0, 1, 0, 0, 1]" +
				 "[0, 0, 1, 1, 0]" +
				 "[0, 0, 0, 0, 0]" +
				 "[0, 0, 0, 0, 1]" +
				 "[0, 1, 1, 0, 0]]");
	assertEquals(result, GraphUtilities.containsCycle(m));
	
//	result = false;
//	m = Matrix.createMatrix("");
//	assertEquals(result, GraphUtilities.containsCycle(m));
    }
    
}
