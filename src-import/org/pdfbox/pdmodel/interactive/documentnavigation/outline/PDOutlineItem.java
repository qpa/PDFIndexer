/**
 * Copyright (c) 2005, www.pdfbox.org
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
package org.pdfbox.pdmodel.interactive.documentnavigation.outline;

import java.io.IOException;
import java.util.List;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSDictionary;
import org.pdfbox.cos.COSFloat;
import org.pdfbox.exceptions.OutlineNotLocalException;
import org.pdfbox.pdmodel.PDDestinationNameTreeNode;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.pdfbox.pdmodel.graphics.color.PDColorSpaceInstance;
import org.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.pdfbox.pdmodel.interactive.action.type.PDAction;
import org.pdfbox.pdmodel.interactive.action.type.PDActionGoTo;
import org.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

/**
 * This represents an outline in a pdf document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.4 $
 */
public class PDOutlineItem extends PDOutlineNode
{   
    
    private static final int ITALIC_FLAG = 1;
    private static final int BOLD_FLAG = 2;
    
    /**
     * Default Constructor.
     */
    public PDOutlineItem()
    {
        super();
    }
    
    /**
     * Constructor for an existing outline item.
     * 
     * @param dic The storage dictionary.
     */
    public PDOutlineItem( COSDictionary dic )
    {
        super( dic );
    }
    
    /**
     * Insert a sibling after this node.
     * 
     * @param item The item to insert.
     */
    public void insertSiblingAfter( PDOutlineItem item )
    {
        item.setParent( getParent() );
        PDOutlineItem next = getNextSibling();
        setNextSibling( item );
        item.setPreviousSibling( this );
        if( next != null )
        {
            item.setNextSibling( next );            
            next.setPreviousSibling( item );
        }
        updateParentOpenCount( 1 );
    }
    
    /**
     * Return the previous sibling or null if there is no sibling.
     * 
     * @return The previous sibling.
     */
    public PDOutlineItem getPreviousSibling()
    {
        PDOutlineItem last = null;
        COSDictionary lastDic = (COSDictionary)node.getDictionaryObject( "Prev" );
        if( lastDic != null )
        {
            last = new PDOutlineItem( lastDic );
        }
        return last;
    }
    
    /**
     * Set the previous sibling, this will be maintained by this class.
     * 
     * @param outlineNode The new previous sibling.
     */
    protected void setPreviousSibling( PDOutlineNode outlineNode )
    {
        node.setItem( "Prev", outlineNode );
    }
    
    /**
     * Return the next sibling or null if there is no next sibling.
     * 
     * @return The next sibling.
     */
    public PDOutlineItem getNextSibling()
    {
        PDOutlineItem last = null;
        COSDictionary lastDic = (COSDictionary)node.getDictionaryObject( "Next" );
        if( lastDic != null )
        {
            last = new PDOutlineItem( lastDic );
        }
        return last;
    }
    
    /**
     * Set the next sibling, this will be maintained by this class.
     * 
     * @param outlineNode The new next sibling.
     */
    protected void setNextSibling( PDOutlineNode outlineNode )
    {
        node.setItem( "Next", outlineNode );
    }
    
    /**
     * Get the title of this node.
     * 
     * @return The title of this node.
     */
    public String getTitle()
    {
        return node.getString( "Title" );
    }
    
    /**
     * Set the title for this node.
     * 
     * @param title The new title for this node.
     */
    public void setTitle( String title )
    {
        node.setString( "Title", title );
    }
    
    /**
     * Get the page destination of this node.
     * 
     * @return The page destination of this node.
     * @throws IOException If there is an error creating the destination.
     */
    public PDDestination getDestination() throws IOException
    {
        return PDDestination.create( node.getDictionaryObject( "Dest" ) );
    }
    
    /**
     * Set the page destination for this node.
     * 
     * @param dest The new page destination for this node.
     */
    public void setDestination( PDDestination dest )
    {
        node.setItem( "Dest", dest );
    }
    
    /**
     * A convenience method that will create an XYZ destination using only the defaults.
     * 
     * @param page The page to refer to.
     */
    public void setDestination( PDPage page )
    {
        PDPageXYZDestination dest = null;
        if( page != null )
        {
            dest = new PDPageXYZDestination();
            dest.setPage( page );
        }
        setDestination( dest );
    }
    
    /**
     * This method will attempt to find the page in this PDF document that this outline points to.
     * If the outline does not point to anything then this method will return null.  If the outline
     * is an action that is not a GoTo action then this methods will throw the OutlineNotLocationException
     * 
     * @param doc The document to get the page from.
     * 
     * @return The page that this outline will go to when activated or null if it does not point to anything.
     * @throws IOException If there is an error when trying to find the page.
     * @throws OutlineNotLocalException If this outline refers to an external page.
     */
    public PDPage findDestinationPage( PDDocument doc ) throws IOException, OutlineNotLocalException
    {
        PDPage page = null;
        PDDestination rawDest = getDestination();
        if( rawDest == null ) 
        {
            PDAction outlineAction = getAction();
            if( outlineAction instanceof PDActionGoTo ) 
            {
                rawDest = ((PDActionGoTo)outlineAction).getDestination();
            }
            else if( outlineAction == null )
            {
                //if the outline action is null then this outline does not refer
                //to anything and we will just return null.
            }
            else
            {
                throw new OutlineNotLocalException( "Error: Outline does not reference a local page." );
            }
        }
        
        PDPageDestination pageDest = null;
        if( rawDest instanceof PDNamedDestination ) 
        {
            //if we have a named destination we need to lookup the PDPageDestination
            PDNamedDestination namedDest = (PDNamedDestination)rawDest;
            PDDocumentNameDictionary namesDict = doc.getDocumentCatalog().getNames();
            if( namesDict != null ) 
            {
                PDDestinationNameTreeNode destsTree = namesDict.getDests();
                if( destsTree != null ) 
                {
                    pageDest = (PDPageDestination)destsTree.getValue( namedDest.getNamedDestination() );
                }
            }
        }
        else if( rawDest instanceof PDPageDestination) 
        {
            pageDest = (PDPageDestination) rawDest;
        }
        else if( rawDest == null )
        {
            //if the destination is null then we will simply return a null page.
        }
        else 
        {
            throw new IOException( "Error: Unknown destination type " + rawDest );
        }
        
        if( pageDest != null )
        {
            page = pageDest.getPage();
            if( page == null )
            {
                int pageNumber = pageDest.getPageNumber();
                if( pageNumber != -1 )
                {
                    List allPages = doc.getDocumentCatalog().getAllPages();
                    page = (PDPage)allPages.get( pageNumber );
                } 
            }
        }
        
        return page;
    }
    
    /**
     * Get the action of this node.
     * 
     * @return The action of this node.
     */
    public PDAction getAction()
    {
        return PDActionFactory.createAction( (COSDictionary)node.getDictionaryObject( "A" ) );
    }
    
    /**
     * Set the action for this node.
     * 
     * @param action The new action for this node.
     */
    public void setAction( PDAction action )
    {
        node.setItem( "A", action );
    }
    
    /**
     * Get the structure element of this node.
     * 
     * @return The structure element of this node.
     */
    public PDStructureElement getStructureElement()
    {
        PDStructureElement se = null;
        COSDictionary dic = (COSDictionary)node.getDictionaryObject( "SE" );
        if( dic != null )
        {
            se = new PDStructureElement( dic );
        }
        return se;
    }
    
    /**
     * Set the structure element for this node.
     * 
     * @param structureElement The new structure element for this node.
     */
    public void setAction( PDStructureElement structureElement )
    {
        node.setItem( "SE", structureElement );
    }
    
    /**
     * Get the text color of this node.  Default is black and this method
     * will never return null.
     * 
     * @return The structure element of this node.
     */
    public PDColorSpaceInstance getTextColor()
    {
        PDColorSpaceInstance retval = null;
        COSArray csValues = (COSArray)node.getDictionaryObject( "C" );
        if( csValues == null )
        {
            csValues = new COSArray();
            csValues.growToSize( 3, new COSFloat( 0 ) );
            node.setItem( "C", csValues );
        }
        retval = new PDColorSpaceInstance(csValues);
        retval.setColorSpace( PDDeviceRGB.INSTANCE );
        return retval;
    }
    
    /**
     * Set the text color for this node.
     * 
     * @param textColor The text color for this node.
     */
    public void setTextColor( PDColorSpaceInstance textColor )
    {
        node.setItem( "C", textColor.getCOSColorSpaceValue() );
    }
    
    /**
     * A flag telling if the text should be italic.
     * 
     * @return The italic flag.
     */
    public boolean isItalic()
    {
        return (node.getInt( "F", 0 ) & ITALIC_FLAG) == ITALIC_FLAG;
    }
    
    /**
     * Set the italic property of the text.
     * 
     * @param italic The new italic flag.
     */
    public void setItalic( boolean italic )
    {
        int f = node.getInt( "F", 0 );
        if( italic )
        {
            f = f | ITALIC_FLAG;
        }
        else
        {
            f = f ^ ITALIC_FLAG;
        }
        node.setInt( "F", f );
    }
    
    /**
     * A flag telling if the text should be bold.
     * 
     * @return The bold flag.
     */
    public boolean isBold()
    {
        return (node.getInt( "F", 0 ) & BOLD_FLAG) == BOLD_FLAG;
    }
    
    /**
     * Set the bold property of the text.
     * 
     * @param bold The new bold flag.
     */
    public void setBold( boolean bold )
    {
        int f = node.getInt( "F", 0 );
        if( bold )
        {
            f = f | BOLD_FLAG;
        }
        else
        {
            f = f ^ BOLD_FLAG;
        }
        node.setInt( "F", f );
    }
     
}
