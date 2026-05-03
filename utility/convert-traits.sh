#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: ./convert.sh <path_to_file.sjson>"
    exit 1
fi

FILE="$1"

# Strip UTF-16 null bytes and carriage returns
tr -d '\000\r' < "$FILE" | awk -F'[:=]' '
{
    key = $1
    val = $2
    
    # Reconstruct the value if the text itself contained an = or :
    for(i=3; i<=NF; i++) val = val FS $i 
    
    # Clean up key and value
    gsub(/^[ \t"]+|[ \t"]+$/, "", key)
    gsub(/^[ \t"]+|[ \t",]+$/, "", val)
    
    # Capture variables for the current block
    if (key == "Id") curr_id = val
    if (key == "DisplayName") curr_name = val
    if (key == "InheritFrom") curr_inherit = val
    
    # When hitting a closing bracket, save the block data to memory
    if ($0 ~ /}/) {
        if (curr_id != "") {
            # Save the ID order so the YAML output matches the file order
            order[++count] = curr_id
            
            # Store Names and Inheritances in dictionaries mapped to the ID
            if (curr_name != "") names[curr_id] = curr_name
            if (curr_inherit != "") inherits[curr_id] = curr_inherit
        }
        
        # Reset for the next block
        curr_id = ""
        curr_name = ""
        curr_inherit = ""
    }
}
END {
    # After the whole file is read, resolve all the data
    for (i = 1; i <= count; i++) {
        id = order[i]
        final_name = ""
        
        # 1. Does it have a DisplayName directly?
        if (id in names) {
            final_name = names[id]
        } 
        # 2. If not, does it inherit from something?
        else if (id in inherits) {
            target = inherits[id]
            depth = 0
            
            # Follow the inheritance chain (limited to 10 levels to prevent infinite loops)
            while (depth < 10) {
                if (target in names) {
                    final_name = names[target]
                    break
                } else if (target in inherits) {
                    target = inherits[target]
                } else {
                    break
                }
                depth++
            }
        }
        
        # Only print if we successfully found a name
        if (final_name != "") {
            printf "- \"%s\": \"%s\"\n", id, final_name
        }
    }
}'