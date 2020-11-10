var JavaPackages = new JavaImporter(
	Packages.ray.rage.scene.Light,
 	Packages.java.awt.Color);
with(JavaPackages){
	function updateAmbientColor(thisLight){
		thisLight.setAmbient(java.awt.Color.blue);
	}
}