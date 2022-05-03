/*
 * Legend is a library that generates a legend.
 * Legend is developed by CNRS http://www.cnrs.fr/.
 *
 * Most of the code had been picked up from Geoserver (https://github.com/geoserver/geoserver). Legend is free software;
 * you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * Legend is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details http://www.gnu.org/licenses.
 *
 *
 *For more information, please consult: http://www.orbisgis.org
 *or contact directly: info_at_orbisgis.org
 *
 */

package org.legend.model;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.StreamingRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Provides methods to build a map buffered image.
 *
 * @author Adrien Bessy
 */
public class Map {

    MapContent map;

    public Map(MapContent map){
        this.map = map;
    }

    /**
     * Paint a map and return a buffered image
     * @return the buffered image
     */
    public BufferedImage paintMap(){
        GTRenderer renderer = new StreamingRenderer();
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        renderer.setJava2DHints(hints);
        java.util.Map<String, Object> rendererParams = new HashMap<>();
        LabelCacheImpl labelCache = new LabelCacheImpl();
        rendererParams.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
        //rendererParams.put(StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY,StreamingRenderer.SCALE_ACCURATE);
        rendererParams.put(StreamingRenderer.LINE_WIDTH_OPTIMIZATION_KEY, Boolean.FALSE);
        rendererParams.put(StreamingRenderer.ADVANCED_PROJECTION_HANDLING_KEY, true);
        //rendererParams.put(StreamingRenderer.CONTINUOUS_MAP_WRAPPING, true);
        renderer.setRendererHints(rendererParams);
        renderer.setMapContent(map);

        Rectangle imageBounds;
        ReferencedEnvelope mapBounds;
        try {
            mapBounds = map.getMaxBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(
                    0, 0, 1000, (int) Math.round(1000 * heightToWidth));
        } catch (Exception e) {
            // failed to access map layers
            throw new RuntimeException(e);
        }
        BufferedImage mapBufferedImage = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = mapBufferedImage.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);
        renderer.paint(gr, imageBounds, mapBounds);
        return mapBufferedImage;
    }

}
