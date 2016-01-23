/*
 * @(#)Token.java	1.2 98/05/04
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package org.siberia.ui.swing.text.java;
import java.io.Serializable;
import sun.tools.java.Constants;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.HashMap;

/**
 * Simple class to represent a lexical token.  This
 * wraps the Constants used by the scanner to provide
 * a convenient class that can be stored as a attribute
 * value.
 *
 * @author  Timothy Prinzing
 * @version 1.2 05/04/98
 */
public class JavaToken implements Serializable
{
    // --- variables -------------------------------------
    public static final int MaximumScanValue  = Constants.INLINENEWINSTANCE + 1;
    
    /** default size for font */
    private static int      DEFAULT_SIZE      = 8;
    
    /** default font name */
    private static String   DEFAULT_FONT_NAME = "Courier";
    
    /** default font */
    public static Font     DEFAULT_FONT      = new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_SIZE);
    
    /** list of fonts */
    private static Font[]   AVAILABLE_FONTS   = null;
    
    /** mappings between class and Colors */
    private Map<Class, Color> colorMappings = null;
    
    /** mappings between class and Fonts */
    private Map<Class, Font>  fontMappings  = null;
    /**
     * Key to be used in AttributeSet's holding a value of JavaToken.
     */
    public static final Object JavaTokenAttribute = new AttributeKey();
    
    String representation;
    
    int scanValue;
    
    private JavaToken(String representation, int scanValue)
    {   this.representation = representation;
	this.scanValue = scanValue;
        
        /** fill colorMapping */
        if ( this.colorMappings == null )
        {   this.colorMappings = new HashMap<Class, Color>();
            
            this.colorMappings.put(Operator.class   , Color.BLACK);
            this.colorMappings.put(Allocator.class  , Color.RED);
            this.colorMappings.put(Value.class      , Color.BLACK);
            this.colorMappings.put(Type.class       , Color.GREEN.darker());
            this.colorMappings.put(Expression.class , Color.BLACK);
            this.colorMappings.put(Statement.class  , Color.BLACK);
            this.colorMappings.put(Declaration.class, Color.BLUE);
            this.colorMappings.put(Modifier.class   , Color.BLUE);
            this.colorMappings.put(Punctuation.class, Color.BLACK);
            this.colorMappings.put(Special.class    , Color.LIGHT_GRAY);
        }
        
        /** fill fontMapping */
        if ( this.fontMappings == null )
        {   this.fontMappings = new HashMap<Class, Font>();
            
            if ( AVAILABLE_FONTS == null )
            {   AVAILABLE_FONTS =   new Font[] {   new Font("Helvetica", Font.PLAIN , DEFAULT_SIZE),
                                                   new Font("Helvetica", Font.BOLD  , DEFAULT_SIZE),
                                                   new Font("Helvetica", Font.ITALIC, DEFAULT_SIZE)
                                               };
            }
            
            this.fontMappings.put(Operator.class   , AVAILABLE_FONTS[0]);
            this.fontMappings.put(Allocator.class  , AVAILABLE_FONTS[0]);
            this.fontMappings.put(Value.class      , AVAILABLE_FONTS[0]);
            this.fontMappings.put(Type.class       , AVAILABLE_FONTS[2]);
            this.fontMappings.put(Expression.class , AVAILABLE_FONTS[0]);
            this.fontMappings.put(Statement.class  , AVAILABLE_FONTS[0]);
            this.fontMappings.put(Declaration.class, AVAILABLE_FONTS[1]);
            this.fontMappings.put(Modifier.class   , AVAILABLE_FONTS[1]);
            this.fontMappings.put(Punctuation.class, AVAILABLE_FONTS[0]);
            this.fontMappings.put(Special.class    , AVAILABLE_FONTS[2]);
        }
        
    }
    
    /**
     * A human presentable form of the token, useful
     * for things like lists, debugging, etc.
     */
    public String toString()
    {   return representation; }
    
    /**
     * Numeric value of this token.  This is the value
     * returned by the scanner and is the tie between 
     * the lexical scanner and the tokens.
     */
    public int getScanValue()
    {   return scanValue; }
    
    /**
     * Specifies the category of the token as a 
     * string that can be used as a label.
     */
    public String getCategory()
    {   String nm = getClass().getName();
	int nmStart = nm.lastIndexOf('.') + 1; // not found results in 0
	return nm.substring(nmStart, nm.length());
    }
    
    /**
     * Returns a hashcode for this set of attributes.
     * @return     a hashcode value for this set of attributes.
     */
    public final int hashCode()
    {   return scanValue; }
            
    /**
     * Compares this object to the specifed object.
     * The result is <code>true</code> if and only if the argument is not 
     * <code>null</code> and is a <code>Font</code> object with the same 
     * name, style, and point size as this font. 
     * @param     obj   the object to compare this font with.
     * @return    <code>true</code> if the objects are equal; 
     *            <code>false</code> otherwise.
     */
    public final boolean equals(Object obj)
    {   if (obj instanceof JavaToken)
        {   JavaToken t = (JavaToken) obj;
	    return (scanValue == t.scanValue);
	}
	return false;
    }
    
    static class AttributeKey {
        private AttributeKey() {
	}
        public String toString() {
	    return "token";
	}
    }
    
    /** return a Font for that token
     *  @return a font
     */
    public Font getFontFor()
    {   if ( this.fontMappings != null )
        {   Font f = this.fontMappings.get(this.getClass());
            
            if ( f == null )
            {   return f; }
        }
        
        return DEFAULT_FONT;
    }
    
    /** returns the color to use with that token
     *  @return a color
     */
    public Color getColorFor()
    {   if ( this.colorMappings != null )
        {   Color c = this.colorMappings.get(this.getClass());
            
            if ( c != null )
            {   return c; }
        }
        return Color.BLACK;
    }   
    
    public static class Operator extends JavaToken
    {   Operator(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Allocator extends JavaToken
    {   Allocator(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Value extends JavaToken
    {   Value(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Type extends JavaToken
    {   Type(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Expression extends JavaToken
    {   Expression(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Statement extends JavaToken
    {   Statement(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Declaration extends JavaToken
    {   Declaration(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Modifier extends JavaToken
    {   Modifier(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Punctuation extends JavaToken
    {   Punctuation(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    public static class Special extends JavaToken
    {   Special(String representation, int scanValue)
        {   super(representation, scanValue); }
    }
    
    /*
     * Operators
     */
    public static final JavaToken COMMA =       new Operator(Constants.opNames[Constants.COMMA], 
							 Constants.COMMA);
    public static final JavaToken ASSIGN =      new Operator(Constants.opNames[Constants.ASSIGN],
							 Constants.ASSIGN);
    public static final JavaToken ASGMUL =      new Operator(Constants.opNames[Constants.ASGMUL],
							 Constants.ASGMUL);
    public static final JavaToken ASGDIV =      new Operator(Constants.opNames[Constants.ASGDIV],
							 Constants.ASGDIV);
    public static final JavaToken ASGREM =      new Operator(Constants.opNames[Constants.ASGREM],
							 Constants.ASGREM);
    public static final JavaToken ASGADD =      new Operator(Constants.opNames[Constants.ASGADD],
							 Constants.ASGADD);
    public static final JavaToken ASGSUB =      new Operator(Constants.opNames[Constants.ASGSUB],
							 Constants.ASGSUB);
    public static final JavaToken ASGLSHIFT =   new Operator(Constants.opNames[Constants.ASGLSHIFT],
							 Constants.ASGLSHIFT);
    public static final JavaToken ASGRSHIFT =   new Operator(Constants.opNames[Constants.ASGRSHIFT],
							 Constants.ASGRSHIFT);
    public static final JavaToken ASGURSHIFT =  new Operator(Constants.opNames[Constants.ASGURSHIFT],
							 Constants.ASGURSHIFT);
    public static final JavaToken ASGBITAND =   new Operator(Constants.opNames[Constants.ASGBITAND],
							 Constants.ASGBITAND);
    public static final JavaToken ASGBITOR =    new Operator(Constants.opNames[Constants.ASGBITOR],
							 Constants.ASGBITOR);
    public static final JavaToken ASGBITXOR =   new Operator(Constants.opNames[Constants.ASGBITOR],
							 Constants.ASGBITOR);
    public static final JavaToken COND =        new Operator(Constants.opNames[Constants.COND],
							 Constants.COND);
    public static final JavaToken OR =          new Operator(Constants.opNames[Constants.OR],
							 Constants.OR);
    public static final JavaToken AND =         new Operator(Constants.opNames[Constants.AND],
							 Constants.AND);
    public static final JavaToken BITOR =       new Operator(Constants.opNames[Constants.BITOR],
							 Constants.BITOR);
    public static final JavaToken BITXOR =      new Operator(Constants.opNames[Constants.BITXOR],
							 Constants.BITXOR);
    public static final JavaToken BITAND =      new Operator(Constants.opNames[Constants.BITAND],
							 Constants.BITAND);
    public static final JavaToken NE =          new Operator(Constants.opNames[Constants.NE],
							 Constants.NE);
    public static final JavaToken EQ =          new Operator(Constants.opNames[Constants.EQ],
							 Constants.EQ);
    public static final JavaToken GE =          new Operator(Constants.opNames[Constants.GE],
							 Constants.GE);
    public static final JavaToken GT =          new Operator(Constants.opNames[Constants.GT],
							 Constants.GT);
    public static final JavaToken LE =          new Operator(Constants.opNames[Constants.LE],
							 Constants.LE);
    public static final JavaToken LT =          new Operator(Constants.opNames[Constants.LT],
							 Constants.LT);
    public static final JavaToken INSTANCEOF =  new Operator(Constants.opNames[Constants.INSTANCEOF],
							 Constants.INSTANCEOF);
    public static final JavaToken LSHIFT =      new Operator(Constants.opNames[Constants.LSHIFT],
							 Constants.LSHIFT);
    public static final JavaToken RSHIFT =      new Operator(Constants.opNames[Constants.RSHIFT],
							 Constants.RSHIFT);
    public static final JavaToken URSHIFT =     new Operator(Constants.opNames[Constants.URSHIFT],
							 Constants.URSHIFT);
    public static final JavaToken ADD =         new Operator(Constants.opNames[Constants.ADD],
							 Constants.ADD);
    public static final JavaToken SUB =         new Operator(Constants.opNames[Constants.SUB],
							 Constants.SUB);
    public static final JavaToken DIV =         new Operator(Constants.opNames[Constants.DIV],
							 Constants.DIV);
    public static final JavaToken REM =         new Operator(Constants.opNames[Constants.REM],
							 Constants.REM);
    public static final JavaToken MUL =         new Operator(Constants.opNames[Constants.MUL],
							 Constants.MUL);
    public static final JavaToken CAST =        new Operator(Constants.opNames[Constants.CAST],
							 Constants.CAST);
    public static final JavaToken POS =         new Operator(Constants.opNames[Constants.POS],
							 Constants.POS);
    public static final JavaToken NEG =         new Operator(Constants.opNames[Constants.NEG],
							 Constants.NEG);
    public static final JavaToken NOT =         new Operator(Constants.opNames[Constants.NOT],
							 Constants.NOT);
    public static final JavaToken BITNOT =      new Operator(Constants.opNames[Constants.BITNOT],
							 Constants.BITNOT);
    public static final JavaToken PREINC =      new Operator(Constants.opNames[Constants.PREINC],
							 Constants.PREINC);
    public static final JavaToken PREDEC =      new Operator(Constants.opNames[Constants.PREDEC],
							 Constants.PREDEC);
    public static final JavaToken NEWARRAY =    new Operator(Constants.opNames[Constants.NEWARRAY],
							 Constants.NEWARRAY);
    public static final JavaToken NEWINSTANCE = new Operator(Constants.opNames[Constants.NEWINSTANCE],
							 Constants.NEWINSTANCE);
    public static final JavaToken NEWFROMNAME = new Operator(Constants.opNames[Constants.NEWFROMNAME],
							 Constants.NEWFROMNAME);
    public static final JavaToken POSTINC =     new Operator(Constants.opNames[Constants.POSTINC],
							 Constants.POSTINC);
    public static final JavaToken POSTDEC =     new Operator(Constants.opNames[Constants.POSTDEC],
							 Constants.POSTDEC);
    public static final JavaToken FIELD =       new Operator(Constants.opNames[Constants.FIELD],
							 Constants.FIELD);
    public static final JavaToken METHOD =      new Operator(Constants.opNames[Constants.METHOD],
							 Constants.METHOD);
    public static final JavaToken ARRAYACCESS = new Operator(Constants.opNames[Constants.ARRAYACCESS],
							 Constants.ARRAYACCESS);
    public static final JavaToken INC =         new Operator(Constants.opNames[Constants.INC],
							 Constants.INC);
    public static final JavaToken DEC =         new Operator(Constants.opNames[Constants.DEC],
							 Constants.DEC);
    public static final JavaToken CONVERT =     new Operator(Constants.opNames[Constants.CONVERT],
							 Constants.CONVERT);
    public static final JavaToken EXPR =        new Operator(Constants.opNames[Constants.EXPR],
							 Constants.EXPR);
    public static final JavaToken ARRAY =       new Operator(Constants.opNames[Constants.ARRAY],
							 Constants.ARRAY);
    public static final JavaToken GOTO =        new Operator(Constants.opNames[Constants.GOTO],
							 Constants.GOTO);
    
    /*
     * Allocator tokens
     */
    public static final JavaToken NEW =         new Allocator(Constants.opNames[Constants.NEW],
							 Constants.NEW);
    /*
     * Value tokens
     */
    public static final JavaToken IDENT =       new Value(Constants.opNames[Constants.IDENT],
						      Constants.IDENT);
    public static final JavaToken BOOLEANVAL =  new Value(Constants.opNames[Constants.BOOLEANVAL],
						      Constants.BOOLEANVAL);
    public static final JavaToken BYTEVAL =     new Value(Constants.opNames[Constants.BYTEVAL],
						      Constants.BYTEVAL);
    public static final JavaToken CHARVAL =     new Value(Constants.opNames[Constants.CHARVAL],
						      Constants.CHARVAL);
    public static final JavaToken SHORTVAL =    new Value(Constants.opNames[Constants.SHORTVAL],
						      Constants.SHORTVAL);
    public static final JavaToken INTVAL =      new Value(Constants.opNames[Constants.INTVAL],
						      Constants.INTVAL);
    public static final JavaToken LONGVAL =     new Value(Constants.opNames[Constants.LONGVAL],
						      Constants.LONGVAL);
    public static final JavaToken FLOATVAL =    new Value(Constants.opNames[Constants.FLOATVAL],
						      Constants.FLOATVAL);
    public static final JavaToken DOUBLEVAL =   new Value(Constants.opNames[Constants.DOUBLEVAL],
						      Constants.DOUBLEVAL);
    public static final JavaToken STRINGVAL =   new Value(Constants.opNames[Constants.STRINGVAL],
						      Constants.STRINGVAL);
    /*
     * Type keywords
     */
    public static final JavaToken BYTE =        new Type(Constants.opNames[Constants.BYTE],
						     Constants.BYTE);
    public static final JavaToken CHAR =        new Type(Constants.opNames[Constants.CHAR],
						     Constants.CHAR);
    public static final JavaToken SHORT =       new Type(Constants.opNames[Constants.SHORT],
						     Constants.SHORT);
    public static final JavaToken INT =         new Type(Constants.opNames[Constants.INT],
						     Constants.INT);
    public static final JavaToken LONG =        new Type(Constants.opNames[Constants.LONG],
						     Constants.LONG);
    public static final JavaToken FLOAT =       new Type(Constants.opNames[Constants.FLOAT],
						     Constants.FLOAT);
    public static final JavaToken DOUBLE =      new Type(Constants.opNames[Constants.DOUBLE],
						     Constants.DOUBLE);
    public static final JavaToken VOID =        new Type(Constants.opNames[Constants.VOID],
						     Constants.VOID);
    public static final JavaToken BOOLEAN =     new Type(Constants.opNames[Constants.BOOLEAN],
						     Constants.BOOLEAN);
    /*
     * Expression keywords
     */
    public static final JavaToken TRUE =        new Expression(Constants.opNames[Constants.TRUE],
							   Constants.TRUE);
    public static final JavaToken FALSE =       new Expression(Constants.opNames[Constants.FALSE],
							   Constants.FALSE);
    public static final JavaToken THIS =        new Expression(Constants.opNames[Constants.THIS],
							   Constants.THIS);
    public static final JavaToken SUPER =       new Expression(Constants.opNames[Constants.SUPER],
							   Constants.SUPER);
    public static final JavaToken NULL =        new Expression(Constants.opNames[Constants.NULL],
							   Constants.NULL);
    /*
     * Statement keywords
     */
    public static final JavaToken IF =             new Statement(Constants.opNames[Constants.IF],
							     Constants.IF);
    public static final JavaToken ELSE =           new Statement(Constants.opNames[Constants.ELSE],
							     Constants.ELSE);
    public static final JavaToken FOR =            new Statement(Constants.opNames[Constants.FOR],
							     Constants.FOR);
    public static final JavaToken WHILE =          new Statement(Constants.opNames[Constants.WHILE],
							     Constants.WHILE);
    public static final JavaToken DO =             new Statement(Constants.opNames[Constants.DO],
							     Constants.DO);
    public static final JavaToken SWITCH =         new Statement(Constants.opNames[Constants.SWITCH],
							     Constants.SWITCH);
    public static final JavaToken CASE =           new Statement(Constants.opNames[Constants.CASE],
							     Constants.CASE);
    public static final JavaToken DEFAULT =        new Statement(Constants.opNames[Constants.DEFAULT],
							     Constants.DEFAULT);
    public static final JavaToken BREAK =          new Statement(Constants.opNames[Constants.BREAK],
							     Constants.BREAK);
    public static final JavaToken CONTINUE =       new Statement(Constants.opNames[Constants.CONTINUE],
							     Constants.CONTINUE);
    public static final JavaToken RETURN =         new Statement(Constants.opNames[Constants.RETURN],
							     Constants.RETURN);
    public static final JavaToken TRY =            new Statement(Constants.opNames[Constants.TRY],
							     Constants.TRY);
    public static final JavaToken CATCH =          new Statement(Constants.opNames[Constants.CATCH],
							     Constants.CATCH);
    public static final JavaToken FINALLY =        new Statement(Constants.opNames[Constants.FINALLY],
							     Constants.FINALLY);
    public static final JavaToken THROW =          new Statement(Constants.opNames[Constants.THROW],
							     Constants.THROW);
    public static final JavaToken STAT =           new Statement(Constants.opNames[Constants.STAT],
							     Constants.STAT);
    public static final JavaToken EXPRESSION =     new Statement(Constants.opNames[Constants.EXPRESSION],
							     Constants.EXPRESSION);
    public static final JavaToken DECLARATION =    new Statement(Constants.opNames[Constants.DECLARATION],
							     Constants.DECLARATION);
    public static final JavaToken VARDECLARATION = new Statement(Constants.opNames[Constants.VARDECLARATION],
							     Constants.VARDECLARATION);
    /*
     * Declaration keywords
     */
    public static final JavaToken IMPORT =         new Declaration(Constants.opNames[Constants.IMPORT],
							       Constants.IMPORT);
    public static final JavaToken CLASS =          new Declaration(Constants.opNames[Constants.CLASS],
							       Constants.CLASS);
    public static final JavaToken EXTENDS =        new Declaration(Constants.opNames[Constants.EXTENDS],
							       Constants.EXTENDS);
    public static final JavaToken IMPLEMENTS =     new Declaration(Constants.opNames[Constants.IMPLEMENTS],
							       Constants.IMPLEMENTS);
    public static final JavaToken INTERFACE =      new Declaration(Constants.opNames[Constants.INTERFACE],
							       Constants.INTERFACE);
    public static final JavaToken PACKAGE =        new Declaration(Constants.opNames[Constants.PACKAGE],
							       Constants.PACKAGE);
    /*
     * Modifier keywords
     */
    public static final JavaToken PRIVATE =        new Modifier(Constants.opNames[Constants.PRIVATE],
							    Constants.PRIVATE);
    public static final JavaToken PUBLIC =         new Modifier(Constants.opNames[Constants.PUBLIC],
							    Constants.PUBLIC);
    public static final JavaToken PROTECTED =      new Modifier(Constants.opNames[Constants.PROTECTED],
							    Constants.PROTECTED);
    public static final JavaToken CONST =          new Modifier(Constants.opNames[Constants.CONST],
							    Constants.CONST);
    public static final JavaToken STATIC =         new Modifier(Constants.opNames[Constants.STATIC],
							    Constants.STATIC);
    public static final JavaToken TRANSIENT =      new Modifier(Constants.opNames[Constants.TRANSIENT],
							    Constants.TRANSIENT);
    public static final JavaToken SYNCHRONIZED =   new Modifier(Constants.opNames[Constants.SYNCHRONIZED],
							    Constants.SYNCHRONIZED);
    public static final JavaToken NATIVE =         new Modifier(Constants.opNames[Constants.NATIVE],
							    Constants.NATIVE);
    public static final JavaToken FINAL =          new Modifier(Constants.opNames[Constants.FINAL],
							    Constants.FINAL);
    public static final JavaToken VOLATILE =       new Modifier(Constants.opNames[Constants.VOLATILE],
							    Constants.VOLATILE);
    public static final JavaToken ABSTRACT =       new Modifier(Constants.opNames[Constants.ABSTRACT],
							    Constants.ABSTRACT);
    /*
     * Punctuation
     */
    public static final JavaToken SEMICOLON =      new Punctuation(Constants.opNames[Constants.SEMICOLON],
							       Constants.SEMICOLON);
    public static final JavaToken COLON =          new Punctuation(Constants.opNames[Constants.COLON],
							       Constants.COLON);
    public static final JavaToken QUESTIONMARK =   new Punctuation(Constants.opNames[Constants.QUESTIONMARK],
							       Constants.QUESTIONMARK);
    public static final JavaToken LBRACE =         new Punctuation(Constants.opNames[Constants.LBRACE],
							       Constants.LBRACE);
    public static final JavaToken RBRACE =         new Punctuation(Constants.opNames[Constants.RBRACE],
							       Constants.RBRACE);
    public static final JavaToken LPAREN =         new Punctuation(Constants.opNames[Constants.LPAREN],
							       Constants.LPAREN);
    public static final JavaToken RPAREN =         new Punctuation(Constants.opNames[Constants.RPAREN],
							       Constants.RPAREN);
    public static final JavaToken LSQBRACKET =     new Punctuation(Constants.opNames[Constants.LSQBRACKET],
							       Constants.LSQBRACKET);
    public static final JavaToken RSQBRACKET =     new Punctuation(Constants.opNames[Constants.RSQBRACKET],
							       Constants.RSQBRACKET);
    public static final JavaToken THROWS =         new Punctuation(Constants.opNames[Constants.THROWS],
							       Constants.THROWS);
    /*
     * Special tokens
     */
    public static final JavaToken ERROR =             new Special(Constants.opNames[Constants.ERROR],
							      Constants.ERROR);
    public static final JavaToken COMMENT =           new Special(Constants.opNames[Constants.COMMENT],
							      Constants.COMMENT);
    public static final JavaToken TYPE =              new Special(Constants.opNames[Constants.TYPE],
							      Constants.TYPE);
    public static final JavaToken LENGTH =            new Special(Constants.opNames[Constants.LENGTH],
							      Constants.LENGTH);
    public static final JavaToken INLINERETURN =      new Special(Constants.opNames[Constants.INLINERETURN],
							      Constants.INLINERETURN);
    public static final JavaToken INLINEMETHOD =      new Special(Constants.opNames[Constants.INLINEMETHOD],
							      Constants.INLINEMETHOD);
    public static final JavaToken INLINENEWINSTANCE = new Special(Constants.opNames[Constants.INLINENEWINSTANCE],
							      Constants.INLINENEWINSTANCE);
    public static final JavaToken UNSCANNED =         new Special("unscanned", MaximumScanValue);
    
    static JavaToken[] operators =
    {   COMMA, ASSIGN, ASGMUL, ASGDIV, ASGREM, ASGADD, ASGSUB, ASGLSHIFT,
	ASGRSHIFT, ASGURSHIFT, ASGBITAND, ASGBITOR, ASGBITXOR, COND, OR, AND,
	BITOR, BITXOR, BITAND, NE, EQ, GE, GT, LE, LT, INSTANCEOF, LSHIFT, 
	RSHIFT, URSHIFT, ADD, SUB, DIV, REM, MUL, CAST, POS, NEG, NOT, BITNOT,
	PREINC, PREDEC, NEWARRAY, NEWINSTANCE, NEWFROMNAME, POSTINC, POSTDEC,
	FIELD, METHOD, ARRAYACCESS, INC, DEC, CONVERT, EXPR, ARRAY, GOTO
    };
    
    static JavaToken[] allocators = {NEW};
    
    static JavaToken[] values =
    {   IDENT, BOOLEANVAL, BYTEVAL, CHARVAL, SHORTVAL, INTVAL, LONGVAL, 
	FLOATVAL, DOUBLEVAL, STRINGVAL
    };
    
    static JavaToken[] types =
    {   BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID, BOOLEAN };
    
    static JavaToken[] expressions =
    {   TRUE, FALSE, THIS, SUPER, NULL };
    
    static JavaToken[] statements =
    {   IF, ELSE, FOR, WHILE, DO, SWITCH, CASE, DEFAULT, BREAK,
	CONTINUE, RETURN, TRY, CATCH, FINALLY, THROW, STAT, EXPRESSION, 
	DECLARATION, VARDECLARATION
    };
    
    static JavaToken[] declarations =
    {   IMPORT, CLASS, EXTENDS, IMPLEMENTS, INTERFACE, PACKAGE };
    
    static JavaToken[] modifiers =
    {   PRIVATE, PUBLIC, PROTECTED, CONST, STATIC, TRANSIENT, SYNCHRONIZED,
	NATIVE, FINAL, VOLATILE, ABSTRACT
    };
    
    static JavaToken[] punctuations =
    {   SEMICOLON, COLON, QUESTIONMARK, LBRACE, RBRACE, LPAREN, 
	RPAREN, LSQBRACKET, RSQBRACKET, THROWS
    };
    
    static JavaToken[] specials =
    {   ERROR, COMMENT, TYPE, LENGTH, INLINERETURN, INLINEMETHOD, INLINENEWINSTANCE, UNSCANNED };
    
    static JavaToken[] all =
    {   COMMA, ASSIGN, ASGMUL, ASGDIV, ASGREM, ASGADD, ASGSUB, ASGLSHIFT,
	ASGRSHIFT, ASGURSHIFT, ASGBITAND, ASGBITOR, ASGBITXOR, COND, OR, AND,
	BITOR, BITXOR, BITAND, NE, EQ, GE, GT, LE, LT, INSTANCEOF, LSHIFT, 
	RSHIFT, URSHIFT, ADD, SUB, DIV, REM, MUL, CAST, POS, NEG, NOT, BITNOT,
	PREINC, PREDEC, NEWARRAY, NEWINSTANCE, NEWFROMNAME, POSTINC, POSTDEC,
	FIELD, METHOD, ARRAYACCESS, NEW, INC, DEC, CONVERT, EXPR, ARRAY, GOTO, 
	IDENT, BOOLEANVAL, BYTEVAL, CHARVAL, SHORTVAL, INTVAL, LONGVAL, 
	FLOATVAL, DOUBLEVAL, STRINGVAL,
	BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID, BOOLEAN,
	TRUE, FALSE, THIS, SUPER, NULL,
	IF, ELSE, FOR, WHILE, DO, SWITCH, CASE, DEFAULT, BREAK,
	CONTINUE, RETURN, TRY, CATCH, FINALLY, THROW, STAT, EXPRESSION, 
	DECLARATION, VARDECLARATION,
	IMPORT, CLASS, EXTENDS, IMPLEMENTS, INTERFACE, PACKAGE,
	PRIVATE, PUBLIC, PROTECTED, CONST, STATIC, TRANSIENT, SYNCHRONIZED,
	NATIVE, FINAL, VOLATILE, ABSTRACT,
	SEMICOLON, COLON, QUESTIONMARK, LBRACE, RBRACE, LPAREN, 
	RPAREN, LSQBRACKET, RSQBRACKET, THROWS,
	ERROR, COMMENT, TYPE, LENGTH, INLINERETURN, INLINEMETHOD, INLINENEWINSTANCE, UNSCANNED
    };
}
