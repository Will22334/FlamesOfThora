package com.thora.core.graphics;


/*This class defines a component that has multiple textures associated with it. This currently is locked to
 * a total of four textures. 
 *@author Will
 *
 */
public class MultiTextureComponent extends TextureComponent {
	
	private TextureComponent[] Textures = new TextureComponent[4];
	
	private TextureComponent activeComponent;
	
	public final void addTextureComponent(TextureComponent region, int position) {
		
		if(Textures[position] != null || position <= Textures.length) {
			
			Textures[position] = region;
			
			while(activeComponent == null) {
				
				activeComponent = region;
				break;
			}
		}
		
	}
	
	public final TextureComponent getTextureRegion(int position) {
		
		if(Textures[position] != null) {
			
			return Textures[position];
			
			
		};
		return null;
		
		
	}
	
	public final void replaceTextureComponent(TextureComponent newcomponent, int position) {
		
		
		if(position <= Textures.length) {
			
			Textures[position] = newcomponent;
		}
		
	}

	public TextureComponent getActiveComponent() {
		return activeComponent;
	}

	public void setActiveComponent(int position) {
		this.activeComponent = Textures[position];
	}

}
