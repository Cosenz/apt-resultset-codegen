Per utilizzare il progetto procedere come di seguito:

1) Aggiungere la dipendenza nel pom:
    <pre>
        <code>
        	&lt;dependency&gt;
        		&lt;groupId&gt;it.cosenzproject.tools&lt;/groupId&gt;
          		&lt;artifactId&gt;apt-resultset-codegen&lt;/artifactId&gt;
          		&lt;version&gt;1.0.0-SNAPSHOT&lt;/version&gt;
        	&lt;/dependency&gt;
        </code>
    </pre>
2) Aggiungere il plugin:

    <pre>
        <code>
            &lt;plugin>
                &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
                &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
                &lt;configuration&gt;
                    &lt;annotationProcessors&gt;
                	    &lt;processor&gt;it.cosenzproject.tools.processor.RSConverterProcessor&lt;/processor&gt;
                	&lt;/annotationProcessors&gt;
                &lt;/configuration&gt;
             &lt;/plugin&gt;
        </code>
     </pre>
    
3) Annotare le classi che si interfacciano con il database con l'annotazione @RSConverter e le proprietà come @RSColumn con al suo interno il 
nome della colonna. Ad esempio:
<pre>
    <code>
        @RSConvert
        public class Person {
        
            @RSColumn("PERSON_ID")
            private Integer id;
            @RSColumn("PERSON_NAME")
            private String name;
            @RSColumn("PERSON_GENDER")
            private String gender;
        
            // Getter and Setter
            ...
    </code>
</pre>

4) Lanciare la compilazione, mvn clean install. Al termina della compilazione verranno creati i convertitori per le entità annotate come al punto 3.