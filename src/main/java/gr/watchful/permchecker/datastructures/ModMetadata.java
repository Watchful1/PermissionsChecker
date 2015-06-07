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

package gr.watchful.permchecker.datastructures;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//import com.google.common.base.Joiner;

//import cpw.mods.fml.common.functions.ModNameFunction;
//import cpw.mods.fml.common.versioning.ArtifactVersion;

/**
 * @author cpw
 *
 */
public class ModMetadata
{
    @SerializedName("modid")
    public String modId;
    public String name;
    public String description = "";

    public String url = "";
    public String updateUrl = "";

    public String logoFile = "";
    public String version = "";
    //public ArrayList<String> authorList;//TODO fix this
    //public ArrayList<String> authors;
    public List<String> authorList = Lists.newArrayList();
    public List<String> authors = Lists.newArrayList();
    public String credits = "";
    public String parent = "";
    public String[] screenshots;

    public ModMetadata()
    {
    }
    
    public String toString() {
    	StringBuilder bldr = new StringBuilder();
    	bldr.append(modId); bldr.append(" : ");
    	bldr.append(name); bldr.append(" : ");
    	bldr.append(authorList.size()); bldr.append(" : ");
    	bldr.append(authors.size());
    	return bldr.toString();
    }
}