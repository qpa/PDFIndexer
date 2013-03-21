/**
 * Copyright (c) 2003, www.pdfbox.org
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
package org.pdfbox.pdmodel.interactive.annotation;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSDictionary;
import org.pdfbox.cos.COSName;

import org.pdfbox.pdmodel.common.COSObjectable;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.interactive.action.PDAdditionalActions;
import org.pdfbox.cos.COSBase;

/**
 * This class represents a PDF annotation.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.7 $
 */
public abstract class PDAnnotation implements COSObjectable
{
    /**
     * An annotation flag.
     */
    public static final int FLAG_INVISIBLE = 1 << 0;
    /**
     * An annotation flag.
     */
    public static final int FLAG_HIDDEN = 1 << 1;
    /**
     * An annotation flag.
     */
    public static final int FLAG_PRINTED = 1 << 2;
    /**
     * An annotation flag.
     */
    public static final int FLAG_NO_ZOOM = 1 << 3;
    /**
     * An annotation flag.
     */
    public static final int FLAG_NO_ROTATE = 1 << 4;
    /**
     * An annotation flag.
     */
    public static final int FLAG_NO_VIEW = 1 << 5;
    /**
     * An annotation flag.
     */
    public static final int FLAG_READ_ONLY = 1 << 6;
    /**
     * An annotation flag.
     */
    public static final int FLAG_LOCKED = 1 << 7;
    /**
     * An annotation flag.
     */
    public static final int FLAG_TOGGLE_NO_VIEW = 1 << 8;
    
    
    
    private COSDictionary dictionary;

    /**
     * Constructor.
     */
    public PDAnnotation()
    {
        dictionary = new COSDictionary();
        dictionary.setItem( COSName.TYPE, COSName.getPDFName( "Annot" ) );
    }

    /**
     * Constructor.
     *
     * @param dict The annotations dictionary.
     */
    public PDAnnotation( COSDictionary dict )
    {
        dictionary = dict;
    }

    /**
     * returns the dictionary.
     * @return the dictionary
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }

    /**
     * The annotation rectangle, defining the location of the annotation
     * on the page in default user space units.  This is usually required and should
     * not return null on valid PDF documents.  But where this is a parent form field
     * with children, such as radio button collections then the rectangle will be null.
     *
     * @return The Rect value of this annotation.
     */
    public PDRectangle getRectangle()
    {
        COSArray rectArray = (COSArray)dictionary.getDictionaryObject( COSName.getPDFName( "Rect" ) );
        PDRectangle rectangle = null;
        if( rectArray != null )
        {
            rectangle = new PDRectangle( rectArray );
        }
        return rectangle;
    }

    /**
     * This will set the rectangle for this annotation.
     *
     * @param rectangle The new rectangle values.
     */
    public void setRectangle( PDRectangle rectangle )
    {
        dictionary.setItem( COSName.getPDFName( "Rect" ), rectangle.getCOSArray() );
    }

   /**
     * This will get the flags for this field.
     *
     * @return flags The set of flags.
     */
    public int getAnnotationFlags()
    {
        return getDictionary().getInt( "F", 0 );
    }

    /**
     * This will set the flags for this field.
     *
     * @param flags The new flags.
     */
    public void setAnnotationFlags( int flags )
    {
        getDictionary().setInt( "F", flags );
    }

    /**
     * Interface method for COSObjectable.
     *
     * @return This object as a standard COS object.
     */
    public COSBase getCOSObject()
    {
        return getDictionary();
    }

    /**
     * This will get the name of the current appearance stream if any.
     *
     * @return The name of the appearance stream.
     */
    public String getAppearanceStream()
    {
        String retval = null;
        COSName name = (COSName)getDictionary().getDictionaryObject( COSName.getPDFName( "AS" ) );
        if( name != null )
        {
            retval = name.getName();
        }
        return retval;
    }

    /**
     * This will set the annotations appearance stream name.
     *
     * @param as The name of the appearance stream.
     */
    public void setAppearanceStream( String as )
    {
        if( as == null )
        {
            getDictionary().removeItem( COSName.getPDFName( "AS" ) );
        }
        else
        {
            getDictionary().setItem( COSName.getPDFName( "AS" ), COSName.getPDFName( as ) );
        }
    }

    /**
     * This will get the appearance dictionary associated with this annotation.
     * This may return null.
     *
     * @return This annotations appearance.
     */
    public PDAppearanceDictionary getAppearance()
    {
        PDAppearanceDictionary ap = null;
        COSDictionary apDic = (COSDictionary)dictionary.getDictionaryObject( COSName.getPDFName( "AP" ) );
        if( apDic != null )
        {
            ap = new PDAppearanceDictionary( apDic );
        }
        return ap;
    }

    /**
     * This will set the appearance associated with this annotation.
     *
     * @param appearance The appearance dictionary for this annotation.
     */
    public void setAppearance( PDAppearanceDictionary appearance )
    {
        COSDictionary ap = null;
        if( appearance != null )
        {
            ap = appearance.getDictionary();
        }
        dictionary.setItem( COSName.getPDFName( "AP" ), ap );
    }
    
    /**
     * Get the invisible flag.
     * 
     * @return The invisible flag.
     */
    public boolean isInvisible()
    {
        return (getAnnotationFlags() & FLAG_INVISIBLE) == FLAG_INVISIBLE;
    }
    
    /**
     * Set the invisible flag.
     * 
     * @param invisible The new invisible flag.
     */
    public void setInvisible( boolean invisible )
    {
        if( invisible )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_INVISIBLE );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_INVISIBLE );
        }
    }
    
    /**
     * Get the hidden flag.
     * 
     * @return The hidden flag.
     */
    public boolean isHidden()
    {
        return (getAnnotationFlags() & FLAG_HIDDEN) == FLAG_HIDDEN;
    }
    
    /**
     * Set the hidden flag.
     * 
     * @param hidden The new hidden flag.
     */
    public void setHidden( boolean hidden )
    {
        if( hidden )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_HIDDEN );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_HIDDEN );
        }
    }
    
    /**
     * Get the printed flag.
     * 
     * @return The printed flag.
     */
    public boolean isPrinted()
    {
        return (getAnnotationFlags() & FLAG_PRINTED) == FLAG_PRINTED;
    }
    
    /**
     * Set the printed flag.
     * 
     * @param printed The new printed flag.
     */
    public void setPrinted( boolean printed )
    {
        if( printed )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_PRINTED );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_PRINTED );
        }
    }
    
    /**
     * Get the noZoom flag.
     * 
     * @return The noZoom flag.
     */
    public boolean isNoZoom()
    {
        return (getAnnotationFlags() & FLAG_NO_ZOOM) == FLAG_NO_ZOOM;
    }
    
    /**
     * Set the noZoom flag.
     * 
     * @param noZoom The new noZoom flag.
     */
    public void setNoZoom( boolean noZoom )
    {
        if( noZoom )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_NO_ZOOM );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_NO_ZOOM );
        }
    }
    
    /**
     * Get the noRotate flag.
     * 
     * @return The noRotate flag.
     */
    public boolean isNoRotate()
    {
        return (getAnnotationFlags() & FLAG_NO_ROTATE) == FLAG_NO_ROTATE;
    }
    
    /**
     * Set the noRotate flag.
     * 
     * @param noRotate The new noRotate flag.
     */
    public void setNoRotate( boolean noRotate )
    {
        if( noRotate )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_NO_ROTATE );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_NO_ROTATE );
        }
    }
    
    /**
     * Get the noView flag.
     * 
     * @return The noView flag.
     */
    public boolean isNoView()
    {
        return (getAnnotationFlags() & FLAG_NO_VIEW) == FLAG_NO_VIEW;
    }
    
    /**
     * Set the noView flag.
     * 
     * @param noView The new noView flag.
     */
    public void setNoView( boolean noView )
    {
        if( noView )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_NO_VIEW );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_NO_VIEW );
        }
    }
    
    /**
     * Get the readOnly flag.
     * 
     * @return The readOnly flag.
     */
    public boolean isReadOnly()
    {
        return (getAnnotationFlags() & FLAG_READ_ONLY) == FLAG_READ_ONLY;
    }
    
    /**
     * Set the readOnly flag.
     * 
     * @param readOnly The new readOnly flag.
     */
    public void setReadOnly( boolean readOnly )
    {
        if( readOnly )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_READ_ONLY );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_READ_ONLY );
        }
    }
    
    /**
     * Get the locked flag.
     * 
     * @return The locked flag.
     */
    public boolean isLocked()
    {
        return (getAnnotationFlags() & FLAG_LOCKED) == FLAG_LOCKED;
    }
    
    /**
     * Set the locked flag.
     * 
     * @param locked The new locked flag.
     */
    public void setLocked( boolean locked )
    {
        if( locked )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_LOCKED );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_LOCKED );
        }
    }
    
    /**
     * Get the toggleNoView flag.
     * 
     * @return The toggleNoView flag.
     */
    public boolean isToggleNoView()
    {
        return (getAnnotationFlags() & FLAG_TOGGLE_NO_VIEW) == FLAG_TOGGLE_NO_VIEW;
    }
    
    /**
     * Set the toggleNoView flag.
     * 
     * @param toggleNoView The new toggleNoView flag.
     */
    public void setToggleNoView( boolean toggleNoView )
    {
        if( toggleNoView )
        {
            setAnnotationFlags( getAnnotationFlags() | FLAG_TOGGLE_NO_VIEW );            
        }
        else
        {
            setAnnotationFlags( getAnnotationFlags() ^ FLAG_TOGGLE_NO_VIEW );
        }
    }
    
    /**
     * Get the additional actions for this field.  This will return null
     * if there are no additional actions for this field.
     * 
     * @return The actions of the field.
     */
    public PDAdditionalActions getActions()
    {
        COSDictionary aa = (COSDictionary)dictionary.getDictionaryObject( "AA" );
        PDAdditionalActions retval = null;
        if( aa != null )
        {
            retval = new PDAdditionalActions( aa );
        }
        return retval;
    }
    
    /**
     * Set the actions of the field.
     * 
     * @param actions The field actions.
     */
    public void setActions( PDAdditionalActions actions )
    {
        dictionary.setItem( "AA", actions );
    }
}