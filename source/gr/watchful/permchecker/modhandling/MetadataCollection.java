/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.ModMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

//import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
//import cpw.mods.fml.common.versioning.ArtifactVersion;
//import cpw.mods.fml.common.versioning.VersionParser;

public class MetadataCollection
{
    @SuppressWarnings("unused")
    private String modListVersion;
    private ModMetadata[] modList;
    private Map<String, ModMetadata> metadatas = Maps.newHashMap();

    public static MetadataCollection from(InputStream inputStream, String sourceName)
    {
        if (inputStream == null) return null;

        InputStreamReader reader = new InputStreamReader(inputStream);
        try
        {
            MetadataCollection collection;
            Gson gson = new GsonBuilder().registerTypeAdapter(ArtifactVersion.class, new ArtifactVersionAdapter()).create();
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(reader);
            if (rootElement.isJsonArray())
            {
                collection = new MetadataCollection();
                JsonArray jsonList = rootElement.getAsJsonArray();
                collection.modList = new ModMetadata[jsonList.size()];
                int i = 0;
                for (JsonElement mod : jsonList)
                {
                	System.out.println(mod);
                    collection.modList[i++]=gson.fromJson(mod, ModMetadata.class);
                }
            }
            else
            {
                collection = gson.fromJson(rootElement, MetadataCollection.class);
            }
            collection.parseModMetadataList();
            return collection;
        }
        catch (Exception e) {
			return null;
		}
    }


    private void parseModMetadataList()
    {
        for (ModMetadata modMetadata : modList)
        {
            metadatas.put(modMetadata.modId, modMetadata);
        }
    }

    public static class ArtifactVersionAdapter extends TypeAdapter<ArtifactVersion>
    {

        @Override
        public void write(JsonWriter out, ArtifactVersion value) throws IOException
        {
            // no op - we never write these out
        }

        @Override
        public ArtifactVersion read(JsonReader in) throws IOException
        {
            return null;//return VersionParser.parseVersionReference(in.nextString());
        }

    }
    
    public String toString() {
    	StringBuilder bldr = new StringBuilder();
    	for(ModMetadata modMeta : metadatas.values()) {
    		bldr.append(modMeta.toString()); bldr.append("\n");
    	}
    	return bldr.toString();
    }
}