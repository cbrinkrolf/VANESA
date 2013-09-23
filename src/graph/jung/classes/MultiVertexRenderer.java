package graph.jung.classes;

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.JComponent;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformerDecorator;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

import javax.swing.*;

/*
** MultiVertexRenderer
** Copyright (C) 2011, Matt Johnson
**
** MultiVertexRenderer.java (Author(s): Matt Johnson)
**
** Permission is hereby granted, free of charge, to any person obtaining a
** copy of this software and associated documentation files (the "Software"),
** to deal in the Software without restriction, including without limitation
** the rights to use, copy, modify, merge, publish, distribute, sublicense,
** and/or sell copies of the Software, and to permit persons to whom the
** Software is furnished to do so, subject to the following conditions:
**
** The above copyright notice and this permission notice shall be included
** in all copies or substantial portions of the Software. Changes in the
** copyright notice and/or disclaimer are illegal.
**
** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
** OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
** FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
** ANY MEMBER OF UNCC'S GAME LAB BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
** WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
** CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */


public class MultiVertexRenderer<V, E> implements Renderer.Vertex<V, E>
{

    public void paintVertex(RenderContext<V, E> rc, Layout<V, E> layout, V v)
    {
        Graph<V, E> graph = layout.getGraph();
        if (rc.getVertexIncludePredicate().evaluate(Context.<Graph<V, E>, V>getInstance(graph, v)))
        {
            paintIconForVertex(rc, v, layout);
        }
    }

    protected void paintIconForVertex(RenderContext<V, E> rc, V v, Layout<V, E> layout)
    {
        GraphicsDecorator g = rc.getGraphicsContext();
        boolean vertexHit;
        // get the shape to be rendered
        Shape shape = rc.getVertexShapeTransformer().transform(v);

        Point2D p = layout.transform(v);
        p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
        float x = (float) p.getX();
        float y = (float) p.getY();
        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
        // transform the vertex shape with xtransform
        shape = xform.createTransformedShape(shape);

        vertexHit = vertexHit(rc, shape);
        //rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

        if (vertexHit)
        {
            if (rc.getVertexIconTransformer() != null)
            {
                Icon icon = rc.getVertexIconTransformer().transform(v);
                if (icon != null)
                {
                    paintShapeForVertex(rc, v, shape); //We added this line!
                    g.draw(icon, rc.getScreenDevice(), shape, (int) x, (int) y);

                } else
                {
                    paintShapeForVertex(rc, v, shape);
                }
            } else
            {
                paintShapeForVertex(rc, v, shape);
            }
        }
    }

    protected boolean vertexHit(RenderContext<V, E> rc, Shape s)
    {
        JComponent vv = rc.getScreenDevice();
        Rectangle deviceRectangle = null;
        if (vv != null)
        {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(
                    0, 0,
                    d.width, d.height);
        }
        MutableTransformer vt = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW);
        if (vt instanceof MutableTransformerDecorator)
        {
            vt = ((MutableTransformerDecorator) vt).getDelegate();
        }
        return vt.transform(s).intersects(deviceRectangle);
    }

    protected void paintShapeForVertex(RenderContext<V, E> rc, V v, Shape shape)
    {
        GraphicsDecorator g = rc.getGraphicsContext();
        Paint oldPaint = g.getPaint();
        Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
        if (fillPaint != null)
        {
            g.setPaint(fillPaint);
            g.fill(shape);
            g.setPaint(oldPaint);
        }
        Paint drawPaint = rc.getVertexDrawPaintTransformer().transform(v);
        if (drawPaint != null)
        {
            g.setPaint(drawPaint);
            Stroke oldStroke = g.getStroke();
            Stroke stroke = rc.getVertexStrokeTransformer().transform(v);
            if (stroke != null)
            {
                g.setStroke(stroke);
            }
            g.draw(shape);
            g.setPaint(oldPaint);
            g.setStroke(oldStroke);
        }
    }
}
