package transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YmlTest {

	public static void main(String[] args) {
		test();

	}

	public static void test() {

		Yaml yaml = new Yaml(new Constructor(YmlRule.class));
		File initialFile = new File("src/transformation/test.yml");
		System.out.println(initialFile.getAbsolutePath());
	    InputStream targetStream;
		try {
			targetStream = new FileInputStream(initialFile);
			//InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.yaml");
			Iterator<Object> it= yaml.loadAll(targetStream).iterator();
			while(it.hasNext()){
				YmlRule r = (YmlRule) it.next();
				System.out.println(r.getRuleName());
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
