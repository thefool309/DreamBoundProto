package com.example.dreambound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

import android.util.Base64;
import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DreamHandler extends DefaultHandler {
    /*
     This is the SAX2 XML parser that interprets the TMX
     file and creates a TileMapData object based on that
     I will be implementing map objectgroup loading
     I will also attempt to implement CSV support
     I am rebuilding the code David made
     from the ground up to make some design changes,
     and for the sake of learning how it works.
    */

    /*
     BASED ON CODE FROM: davidmi/Android-TMX-Loader https://github.com/davidmi/Android-TMX-Loader
     TMX LOADER FOR ANDROID Second beta release
     0.8.1 Written by David Iserovich
     Big thank you to David who did a lot of the work for me.
    */

    //Member Fields

    private boolean inMap, inTileSet, inTile, inLayer, parsingData, inObjectGroup, inObject, inProperties, inProperty, inImage; //flags for which tag we are in

    DreamMapData.DreamTMXObject currentTMXObject;

    DreamMapData.DreamTileSet currentTileSet;
    DreamMapData.DreamLayer currentLayer;
    DreamMapData.DreamObjectGroup currentObjectGroup;

    HashMap<String, DreamMapData.DreamProperty> currentTileSetProperties;
    HashMap<String, DreamMapData.DreamProperty> currentLayerProperties;

    private DreamMapData mapData;

    private int currentColumn = 0, currentRow = 0;

    private String encoding;    //string to hold type of encoding
    private StringBuilder dataBuilder = new StringBuilder();      //String Builder to create data with
    private String compression;     //string to hold type of compression for base64


    //constructor
    public DreamHandler() { super(); }

    //accessor
    public DreamMapData getTileMapData() { return mapData; }

    @Override
    public void startDocument() { mapData = new DreamMapData(); }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Log.i("Element Started", "element: " + qName);

        //startElement() takes in the XML element (tag) that is currently being parsed
        //and compares it to this conditional to decide what attributes to pull
        //Read more on SAX2 parser if you're interested in the specifics of how this works
        switch (localName) {        // instead of chaining if/else statements together
            case "map":             //I used a switch/case this is to improve readability
                inMap = true;
                if (!(attributes.getValue("orientation").equals("orthogonal"))) {
                    throw new SAXException("Unsupported orientation. Parse Terminated.");
                }
                mapData.orientation = attributes.getValue("orientation");
                Log.d("Checking", mapData.orientation);
                mapData.height = Integer.parseInt(attributes.getValue("height"));
                mapData.width = Integer.parseInt(attributes.getValue("width"));
                mapData.tilewidth = Integer.parseInt(attributes.getValue("tilewidth"));
                mapData.tileheight = Integer.parseInt(attributes.getValue("tileheight"));
                break;
            case "tileset":
                inTileSet = true;
                currentTileSet = new  DreamMapData.DreamTileSet();
                currentTileSet.firstGID = Integer.parseInt(attributes.getValue("firstgid"));
                currentTileSet.tileWidth = Integer.parseInt(attributes.getValue("tilewidth"));
                currentTileSet.tileHeight = Integer.parseInt(attributes.getValue("tileheight"));
                currentTileSet.name = attributes.getValue("name");
                currentTileSetProperties = new HashMap<>();
                inTileSet = true;
                break;
            case "image":
                inImage = true;
                currentTileSet.imageFilename = attributes.getValue("source");
                currentTileSet.imageWidth = Integer.parseInt(attributes.getValue("width"));
                currentTileSet.imageHeight = Integer.parseInt(attributes.getValue("height"));
                break;
            case "layer":
                inLayer = true;
                currentLayer = new DreamMapData.DreamLayer();
                currentLayer.name = attributes.getValue("name");
                currentLayer.width = Integer.parseInt(attributes.getValue("width"));
                currentLayer.height = Integer.parseInt(attributes.getValue("height"));
                if (attributes.getValue("opacity") != null) currentLayer.opacity = Double.parseDouble(attributes.getValue("opacity"));
                currentLayer.tiles = new long[currentLayer.height][currentLayer.width];

                currentLayerProperties = new HashMap<>();
                break;
            case "data":
                parsingData = true;
                encoding = attributes.getValue("encoding");
                dataBuilder.setLength(0);
                compression = attributes.getValue("compression");
                break;
            case "tile":
                inTile = true;
                //get the GID
                long gid = Long.parseLong(attributes.getValue("gid"));
                //set tile
                currentLayer.setTile(currentColumn, currentRow, gid);
                //increment column
                currentColumn++;
                //if column is >= width
                if (currentColumn >= currentLayer.width){
                    currentColumn = 0;  //set column to zero
                    currentRow++;       //increment row
                }
            case "objectgroup":
                //TODO: implement object group logic
                inObjectGroup = true;   //create new object group
                currentObjectGroup = new DreamMapData.DreamObjectGroup(attributes.getValue("name"), Integer.parseInt(attributes.getValue("id")));
                break;
            case "object":
                //object logic
                inObject = true;
                //create object
                currentTMXObject = new DreamMapData.DreamTMXObject(Float.parseFloat(attributes.getValue("x")),
                                                                    Float.parseFloat(attributes.getValue("y")),
                                                                    Float.parseFloat(attributes.getValue("width")),
                                                                    Float.parseFloat(attributes.getValue("height")),
                                                                    attributes.getValue("name"));
                //add object to dreamMapData.objects
                currentObjectGroup.objects.add(currentTMXObject);
                break;
            case "properties":
                // properties logic
                inProperties = true;
                break;
            case "property":
                inProperty = true;
                if (inObject) {
                    currentTMXObject.properties.putIfAbsent(attributes.getValue("name"),
                                                    new DreamMapData.DreamProperty(attributes.getValue("type"),
                                                                                   attributes.getValue("value"),
                                                                                   attributes.getValue("name")));
                }
                else if (inLayer) {
                    currentLayer.properties.putIfAbsent(attributes.getValue("name"),
                                                        new DreamMapData.DreamProperty(attributes.getValue("type"),
                                                                                       attributes.getValue("value"),
                                                                                       attributes.getValue("name")));
                }
                else if (inObjectGroup) {
                    currentObjectGroup.properties.putIfAbsent(attributes.getValue("name"),
                                                              new DreamMapData.DreamProperty(attributes.getValue("type"),
                                                                                             attributes.getValue("value"),
                                                                                             attributes.getValue("name")));
                }
                break;
            default:
                Log.e("Unexpected value: ", "Unexpected value: " + localName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Log.i("Element Ended", "element: " + qName);
        switch (localName) {            //end element executes when the end of an element is reached
            case "map":
                inMap = false;
                break;
            case "tileset":
                inTileSet = false;
                break;
            case "data":
                if (encoding.equals("csv")) {   //check encoding
                    try {
                        processCSV();
                    } catch (IOException e) {
                        Log.e("CSV Parsing Error", "error" + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
                else if (encoding.equals("base64")) {
                    try {
                        processBase64Data();
                    } catch(IOException e) {
                        Log.e("base64 IO exception", "error" + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
                parsingData = false;
                break;
            case "image":
                inImage = false;
                break;
            case "layer":
                inLayer = false;
                currentLayer = null;
                break;
            case "tile":
                inTile = false;
                break;
            case "objectgroup":
                inObjectGroup = false;  //add object group to map data
                mapData.objectGroups.add(currentObjectGroup);
                currentObjectGroup = null;
                break;
            case "object":
                inObject = false;
                currentTMXObject = null;
                break;
            case "properties":
                inProperties = false;
                break;
            case "property":
                inProperty = false;
                break;

        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        //characters goes of when characters are detected in-between an element
        Log.i("Characters_In_Element Ended", "characters: " + new String(ch, start, length));
        //accumulate the character data
        dataBuilder.append(new String(ch, start, length));
    }

    private void processCSV() throws IOException {
        String data = dataBuilder.toString();
        //remove newlines and carriage returns
        data = data.replace("\n", "").replace("\r", "");

        String[] tiles = data.split(",");
        int width = currentLayer.width;  // Width of the layer (in tiles)

        //iterate over each tile value
        for (String tile : tiles) {
            long tileValue = Long.parseLong(tile);
            //set the tile in the current layer at position
            currentLayer.setTile(currentColumn, currentRow, tileValue);

            currentColumn++; //increment column

            //once we hit the end of the row
            if (currentColumn >= width) {
                currentColumn = 0; //reset column
                currentRow++; //increment row
            }
        }

        //clear the StringBuilder for the next data block
        dataBuilder.setLength(0);
    }

    private void processBase64Data() throws IOException {
        String data = dataBuilder.toString();
        //Decode base64 data into a byte array
        byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);    //must use android.util.Base64 for compatibility

        //Compression handling
        if (compression.equals("gzip")) {
            // GZIP decompression
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(decodedBytes));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            decodedBytes = outputStream.toByteArray();  // Overwrite decodedBytes with decompressed data
            gzipInputStream.close();
            outputStream.close();
        }
        else if (compression.equals("zlib")) {
            // Zlib decompression
            Inflater inflater = new Inflater();
            inflater.setInput(decodedBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(decodedBytes.length);
            byte[] buffer = new byte[1024];
            int len = 0;
            try {
                while ((len = inflater.inflate(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
            } catch (DataFormatException e) {
                Log.e("Data Format Exception", "error" + e.getMessage());
            } finally {
                inflater.end();
                outputStream.close();
            }


            decodedBytes = outputStream.toByteArray();  // Overwrite decodedBytes with decompressed data
            inflater.end();
            outputStream.close();
        }

        ByteBuffer buffer = ByteBuffer.wrap(decodedBytes); //buffer for decoded bytes

        //While loop to Iterate through each 32-bit GID (4 bytes per ID)
        while (buffer.remaining() >= 4) {
            long gid = buffer.getInt() & 0xFFFFFFFFL; //take in GID

            currentLayer.setTile(currentColumn, currentRow, gid);
            currentColumn++;
            if (currentColumn >= currentLayer.width){
                currentColumn = 0;
                currentRow++;
            }

        }
        dataBuilder.setLength(0);
    }
}
