/**
 * Copyright (c) 2003-2004, www.pdfbox.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdfbox; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.pdfbox.org
 *
 */
package org.pdfbox.pdmodel.common;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSBase;
import org.pdfbox.cos.COSDictionary;
import org.pdfbox.cos.COSInteger;
import org.pdfbox.cos.COSFloat;
import org.pdfbox.cos.COSString;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSNull;
import org.pdfbox.cos.COSNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This is an implementation of a List that will sync its contents to a COSArray.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.10 $
 */
public class COSArrayList implements List
{
    private COSArray array;
    private List actual;
    
    private COSDictionary parentDict;
    private String dictKey;

    /**
     * Constructor.
     *
     * @param actualList The list of standard java objects
     * @param cosArray The COS array object to sync to.
     */
    public COSArrayList( List actualList, COSArray cosArray )
    {
        actual = actualList;
        array = cosArray;
    }
    
    /**
     * This is a really special constructor.  Sometimes the PDF spec says 
     * that a dictionary entry can either be a single item or an array of those
     * items.  But in the PDModel interface we really just want to always return
     * a java.util.List.  In the case were we get the list and never modify it
     * we don't want to convert to COSArray and put one element, unless we append
     * to the list.  So here we are going to create this object with a single
     * item instead of a list, but allow more items to be added and then converted
     * to an array.
     *
     * @param actualObject The PDModel object.
     * @param item The COS Model object.
     * @param dictionary The dictionary that holds the item, and will hold the array if an item is added.
     * @param dictionaryKey The key into the dictionary to set the item.
     */
    public COSArrayList( Object actualObject, COSBase item, COSDictionary dictionary, String dictionaryKey )
    {
        array = new COSArray();
        array.add( item );
        actual = new ArrayList();
        actual.add( actualObject );
        
        parentDict = dictionary;
        dictKey = dictionaryKey;
    }

    /**
     * @see List#size()
     */
    public int size()
    {
        return actual.size();
    }

    /**
     * @see List#isEmpty()
     */
    public boolean isEmpty()
    {
        return actual.isEmpty();
    }

    /**
     * @see List#contains( Object )
     */
    public boolean contains(Object o)
    {
        return actual.contains(o);
    }

    /**
     * @see List#iterator()
     */
    public Iterator iterator()
    {
        return actual.iterator();
    }

    /**
     * @see List#toArray()
     */
    public Object[] toArray()
    {
        return actual.toArray();
    }

    /**
     * @see List#toArray( Object[] )
     */
    public Object[] toArray(Object[] a)
    {
        return actual.toArray(a);

    }

    /**
     * @see List#add( Object )
     */
    public boolean add(Object o)
    {
        //when adding if there is a parentDict then change the item 
        //in the dictionary from a single item to an array.
        if( parentDict != null )
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        //string is a special case because we can't subclass to be COSObjectable
        if( o instanceof String )
        {
            array.add( new COSString( (String)o ) );
        }
        else if( o instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)o;
            array.add( dual.getFirstCOSObject() );
            array.add( dual.getSecondCOSObject() );
        }
        else
        {
            array.add( ((COSObjectable)o).getCOSObject() );
        }
        return actual.add(o);
    }

    /**
     * @see List#remove( Object )
     */
    public boolean remove(Object o)
    {
        if( o instanceof String )
        {
            array.remove( new COSString( (String)o ) );
        }
        else if( o instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)o;
            array.remove( dual.getFirstCOSObject() );
            array.remove( dual.getSecondCOSObject() );
        }
        else
        {
            array.remove( ((COSObjectable)o).getCOSObject() );
        }

        return actual.remove( o );
    }

    /**
     * @see List#containsAll( Collection )
     */
    public boolean containsAll(Collection c)
    {
        return actual.containsAll( c );
    }

    /**
     * @see List#addAll( Collection )
     */
    public boolean addAll(Collection c)
    {
        //when adding if there is a parentDict then change the item 
        //in the dictionary from a single item to an array.
        if( parentDict != null && c.size() > 0)
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        array.addAll( toCOSObjectList( c ) );
        return actual.addAll( c );
    }

    /**
     * @see List#addAll( int, Collection )
     */
    public boolean addAll(int index, Collection c)
    {
        //when adding if there is a parentDict then change the item 
        //in the dictionary from a single item to an array.
        if( parentDict != null && c.size() > 0)
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        
        if( c.size() >0 && c.toArray()[0] instanceof DualCOSObjectable )
        {
            array.addAll( index*2, toCOSObjectList( c ) );
        }
        else
        {
            array.addAll( index, toCOSObjectList( c ) );
        }
        return actual.addAll( index, c );
    }

    /**
     * This will take an array of COSNumbers and return a COSArrayList of
     * java.lang.Integer values.
     *
     * @param intArray The existing integer Array.
     *
     * @return A list that is part of the core Java collections.
     */
    public static List convertIntegerCOSArrayToList( COSArray intArray )
    {
        List numbers = new ArrayList();
        for( int i=0; i<intArray.size(); i++ )
        {
            numbers.add( new Integer( ((COSNumber)intArray.get( i )).intValue() ) );
        }
        return new COSArrayList( numbers, intArray );
    }

    /**
     * This will take an array of COSNumbers and return a COSArrayList of
     * java.lang.Float values.
     *
     * @param floatArray The existing float Array.
     *
     * @return The list of Float objects.
     */
    public static List convertFloatCOSArrayToList( COSArray floatArray )
    {
        List retval = null;
        if( floatArray != null )
        {
            List numbers = new ArrayList();
            for( int i=0; i<floatArray.size(); i++ )
            {
                numbers.add( new Float( ((COSNumber)floatArray.get( i )).floatValue() ) );
            }
            retval = new COSArrayList( numbers, floatArray );
        }
        return retval;
    }

    /**
     * This will take an array of COSName and return a COSArrayList of
     * java.lang.String values.
     *
     * @param nameArray The existing name Array.
     *
     * @return The list of String objects.
     */
    public static List convertCOSNameCOSArrayToList( COSArray nameArray )
    {
        List retval = null;
        if( nameArray != null )
        {
            List names = new ArrayList();
            for( int i=0; i<nameArray.size(); i++ )
            {
                names.add( ((COSName)nameArray.getObject( i )).getName() );
            }
            retval = new COSArrayList( names, nameArray );
        }
        return retval;
    }

    /**
     * This will take an array of COSString and return a COSArrayList of
     * java.lang.String values.
     *
     * @param stringArray The existing name Array.
     *
     * @return The list of String objects.
     */
    public static List convertCOSStringCOSArrayToList( COSArray stringArray )
    {
        List retval = null;
        if( stringArray != null )
        {
            List string = new ArrayList();
            for( int i=0; i<stringArray.size(); i++ )
            {
                string.add( ((COSString)stringArray.getObject( i )).getString() );
            }
            retval = new COSArrayList( string, stringArray );
        }
        return retval;
    }

    /**
     * This will take an list of string objects and return a COSArray of COSName
     * objects.
     *
     * @param strings A list of strings
     *
     * @return An array of COSName objects
     */
    public static COSArray convertStringListToCOSNameCOSArray( List strings )
    {
        COSArray retval = new COSArray();
        for( int i=0; i<strings.size(); i++ )
        {
            Object next = strings.get( i );
            if( next instanceof COSName )
            {
                retval.add( (COSName)next );
            }
            else
            {
                retval.add( COSName.getPDFName( (String)next ) );
            }
        }
        return retval;
    }

    /**
     * This will take an list of string objects and return a COSArray of COSName
     * objects.
     *
     * @param strings A list of strings
     *
     * @return An array of COSName objects
     */
    public static COSArray convertStringListToCOSStringCOSArray( List strings )
    {
        COSArray retval = new COSArray();
        for( int i=0; i<strings.size(); i++ )
        {
            retval.add( new COSString( (String)strings.get( i ) ) );
        }
        return retval;
    }

    /**
     * This will convert a list of COSObjectables to an
     * array list of COSBase objects.
     *
     * @param cosObjectableList A list of COSObjectable.
     *
     * @return A list of COSBase.
     */
    public static COSArray converterToCOSArray( List cosObjectableList )
    {
        COSArray array = null;
        if( cosObjectableList != null )
        {
            array = new COSArray();
            Iterator iter = cosObjectableList.iterator();
            while( iter.hasNext() )
            {
                Object next = iter.next();
                if( next instanceof String )
                {
                    array.add( new COSString( (String)next ) );
                }
                else if( next instanceof Integer || next instanceof Long )
                {
                    array.add( new COSInteger( ((Number)next).longValue() ) );
                }
                else if( next instanceof Float || next instanceof Double )
                {
                    array.add( new COSFloat( ((Number)next).floatValue() ) );
                }
                else if( next instanceof COSObjectable )
                {
                    COSObjectable object = (COSObjectable)next;
                    array.add( object.getCOSObject() );
                }
                else if( next instanceof DualCOSObjectable )
                {
                    DualCOSObjectable object = (DualCOSObjectable)next;
                    array.add( object.getFirstCOSObject() );
                    array.add( object.getSecondCOSObject() );
                }
                else if( next == null )
                {
                    array.add( COSNull.NULL );
                }
                else
                {
                    throw new RuntimeException( "Error: Don't know how to convert type to COSBase '" +
                    next.getClass().getName() + "'" );
                }
            }
        }
        return array;
    }

    private List toCOSObjectList( Collection list )
    {
        List cosObjects = new ArrayList();
        Iterator iter = list.iterator();
        while( iter.hasNext() )
        {
            Object next = iter.next();
            if( next instanceof String )
            {
                cosObjects.add( new COSString( (String)next ) );
            }
            else if( next instanceof DualCOSObjectable )
            {
                DualCOSObjectable object = (DualCOSObjectable)next;
                array.add( object.getFirstCOSObject() );
                array.add( object.getSecondCOSObject() );
            }
            else
            {
                COSObjectable cos = (COSObjectable)next;
                cosObjects.add( cos.getCOSObject() );
            }
        }
        return cosObjects;
    }

    /**
     * @see List#removeAll( Collection )
     */
    public boolean removeAll(Collection c)
    {
        array.removeAll( toCOSObjectList( c ) );
        return actual.removeAll( c );
    }

    /**
     * @see List#retainAll( Collection )
     */
    public boolean retainAll(Collection c)
    {
        array.retainAll( toCOSObjectList( c ) );
        return actual.retainAll( c );
    }

    /**
     * @see List#clear()
     */
    public void clear()
    {
        //when adding if there is a parentDict then change the item 
        //in the dictionary from a single item to an array.
        if( parentDict != null )
        {
            parentDict.setItem( dictKey, (COSBase)null );
        }
        actual.clear();
        array.clear();
    }

    /**
     * @see List#equals( Object )
     */
    public boolean equals(Object o)
    {
        return actual.equals( o );
    }

    /**
     * @see List#hashCode()
     */
    public int hashCode()
    {
        return actual.hashCode();
    }

    /**
     * @see List#get( int )
     */
    public Object get(int index)
    {
        return actual.get( index );

    }

    /**
     * @see List#set( int, Object )
     */
    public Object set(int index, Object element)
    {
        if( element instanceof String )
        {
            COSString item = new COSString( (String)element );
            if( parentDict != null && index == 0 )
            {
                parentDict.setItem( dictKey, item );
            }
            array.set( index, item );
        }
        else if( element instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)element;
            array.set( index*2, dual.getFirstCOSObject() );
            array.set( index*2+1, dual.getSecondCOSObject() );
        }
        else
        {
            if( parentDict != null && index == 0 )
            {
                parentDict.setItem( dictKey, ((COSObjectable)element).getCOSObject() );
            }
            array.set( index, ((COSObjectable)element).getCOSObject() );
        }
        return actual.set( index, element );
    }

    /**
     * @see List#add( int, Object )
     */
    public void add(int index, Object element)
    {
        //when adding if there is a parentDict then change the item 
        //in the dictionary from a single item to an array.
        if( parentDict != null )
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        actual.add( index, element );
        if( element instanceof String )
        {
            array.add( index, new COSString( (String)element ) );
        }
        else if( element instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)element;
            array.add( index*2, dual.getFirstCOSObject() );
            array.add( index*2+1, dual.getSecondCOSObject() );
        }
        else
        {
            array.add( index, ((COSObjectable)element).getCOSObject() );
        }
    }

    /**
     * @see List#remove( int )
     */
    public Object remove(int index)
    {
        if( array.size() > index && array.get( index ) instanceof DualCOSObjectable )
        {
            //remove both objects
            array.remove( index );
            array.remove( index );
        }
        else
        {
            array.remove( index );
        }
        return actual.remove( index );
    }

    /**
     * @see List#indexOf( Object )
     */
    public int indexOf(Object o)
    {
        return actual.indexOf( o );
    }

    /**
     * @see List#lastIndexOf( Object )
     */
    public int lastIndexOf(Object o)
    {
        return actual.indexOf( o );

    }

    /**
     * @see List#listIterator()
     */
    public ListIterator listIterator()
    {
        return actual.listIterator();
    }

    /**
     * @see List#listIterator( int )
     */
    public ListIterator listIterator(int index)
    {
        return actual.listIterator( index );
    }

    /**
     * @see List#subList( int, int )
     */
    public List subList(int fromIndex, int toIndex)
    {
        return actual.subList( fromIndex, toIndex );
    }
}