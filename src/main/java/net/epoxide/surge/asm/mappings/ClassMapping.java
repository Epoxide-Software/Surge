package net.epoxide.surge.asm.mappings;

public class ClassMapping extends Mapping {
    
    public ClassMapping(String name) {
        
        super(name, "");
    }
    
    public boolean isEqual (String className) {
        
        return className.equals(this.toString());
    }
}