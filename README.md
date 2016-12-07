Permissions Checker
===================

Simple permissions checker for Minecraft mods. It will be used to check FTB 3rd Party modpack's for any mods they do not have permission to use. It will help reduce the time it takes to get through all the modpacks that want to be updated.


Mod permission fields
=====================

Name: The name of the mod  
Author: The author of the mod  
Link: The link to the primary mod page  
License Link: If the license is not at the main link, this link goes to the license  
License Image: A link to an image of the license, in case the main link goes down or is changed  
Private License Link: If the license for private packs is different from public packs, and the link to that license is different from the previous link, this link goes to it  
Private License Image Link: The links to an image of the private license, if it exists as above  
Custom Link: This links to an image of the author giving permission for the mod, used if permission is not open. Alternatively, it links to an image of the pack author notifying the author of the use of the mod if the permission is notify  

ShortName: This is used internally to uniquely identify a mod  
Is Public Perm: This is used internally to signify whether the custom link gives public or private perms for a mod  
Mod Version: This is used internally to track the current version of a mod in a pack  

Public Policy: This is one of the below options that signifies the public policy of the mod  
Private Policy: This is one of the below options that signifies the private policy of the mod  

Permissions Policies:  
Open: The author has stated that anyone may distribute this mod in modpacks  
Notify: The author has stated that they must be notified before this mod may be distributed  
FTB: The author has stated that anyone may distribute this mod in modpacks on the FTB launcher  
Request: The author has stated that you must receive explicit permission from them to distribute this mod in modpacks  
Closed: The author has stated that no one may distribute their mod in modpacks. In some extreme cases authors have received special permission  