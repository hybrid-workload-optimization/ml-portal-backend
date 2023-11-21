package test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import kr.co.strato.global.util.Base64Util;

public class Main {

	public static void main(String[] args) {
		String str = "YXBpVmVyc2lvbjogYmF0Y2gvdjEKa2luZDogSm9iCm1ldGFkYXRhOgogIG5hbWU6IHNlcnZpbmcKICBuYW1lc3BhY2U6IGRlZmF1bHQKc3BlYzoKICB0ZW1wbGF0ZToKICAgIG1ldGFkYXRhOgogICAgICBuYW1lOiBzZXJ2aW5nCiAgICBzcGVjOgogICAgICBjb250YWluZXJzOgogICAgICAtIG5hbWU6IGNvdW50ZXIKICAgICAgICBpbWFnZTogY2VudG9zOjcKICAgICAgICBjb21tYW5kOgogICAgICAgICAtICJiaW4vYmFzaCIKICAgICAgICAgLSAiLWMiCiAgICAgICAgIC0gImZvciBpIGluIDkgOCA3IDYgNSA0IDMgMiAxIDsgZG8gZWNobyAkaSA7IGRvbmUiCiAgICAgIHJlc3RhcnRQb2xpY3k6IE5ldmVyCi0tLQphcGlWZXJzaW9uOiBiYXRjaC92MQpraW5kOiBKb2IKbWV0YWRhdGE6CiAgbmFtZTogZGF0YXNldC1kb3dubG9hZAogIG5hbWVzcGFjZTogZGVmYXVsdApzcGVjOgogIHRlbXBsYXRlOgogICAgbWV0YWRhdGE6CiAgICAgIG5hbWU6IGRhdGFzZXQtZG93bmxvYWQKICAgIHNwZWM6CiAgICAgIGNvbnRhaW5lcnM6CiAgICAgIC0gbmFtZTogY291bnRlcgogICAgICAgIGltYWdlOiBjZW50b3M6NwogICAgICAgIGNvbW1hbmQ6CiAgICAgICAgIC0gImJpbi9iYXNoIgogICAgICAgICAtICItYyIKICAgICAgICAgLSAiZm9yIGkgaW4gOSA4IDcgNiA1IDQgMyAyIDEgOyBkbyBlY2hvICRpIDsgZG9uZSIKICAgICAgcmVzdGFydFBvbGljeTogTmV2ZXIKLS0tCmFwaVZlcnNpb246IGJhdGNoL3YxCmtpbmQ6IEpvYgptZXRhZGF0YToKICBuYW1lOiB0cmFpbmluZy1tb2RlbAogIG5hbWVzcGFjZTogZGVmYXVsdApzcGVjOgogIHRlbXBsYXRlOgogICAgbWV0YWRhdGE6CiAgICAgIG5hbWU6IHRyYWluaW5nLW1vZGVsCiAgICBzcGVjOgogICAgICBjb250YWluZXJzOgogICAgICAtIG5hbWU6IGNvdW50ZXIKICAgICAgICBpbWFnZTogY2VudG9zOjcKICAgICAgICBjb21tYW5kOgogICAgICAgICAtICJiaW4vYmFzaCIKICAgICAgICAgLSAiLWMiCiAgICAgICAgIC0gImZvciBpIGluIDkgOCA3IDYgNSA0IDMgMiAxIDsgZG8gZWNobyAkaSA7IGRvbmUiCiAgICAgIHJlc3RhcnRQb2xpY3k6IE5ldmVy";
	
		String yamlStr = Base64Util.decode(str);
		
		InputStream is = new ByteArrayInputStream(yamlStr.getBytes());
		
		
		Yaml yaml = new Yaml();
		
		List<String> data = yaml.load(is);
		for(String d : data) {
			System.out.println(d);
		}
		
		/*
		for(Object object : yaml.loadAll(yamlStr)) {
			Map map = (Map) object;
			String output = new Yaml().dump(map);
			System.out.println(output);
		}
		*/
	}
}
