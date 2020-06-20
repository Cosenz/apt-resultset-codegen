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

Può capitare di avere l'esigenza di mappare i campi del database con delle logiche aziendali.
Per questo si può usare la property <i>converter</i> dell'annotation <code>@RSColumn</code> per indicare quale metodo deve essere richiamato per effettuare il mapping.

Nota: Il metodo della classe da chiamare deve essere statico

Ad esempio:
Create il vostro convertitore

<pre>
    <code>
    public class MyCustomConverter {
        
        public static BigDecimal convert(ResultSet rs) {
            return rs.getBigDecimal("AMNT_1").add(rs.getBigDecimal("AMNT_2"));
        }
    }
    </code>
</pre>

Inserite il percorso completo nella property <i>converter</i>
<pre>
    <code>
        @RSColumn(converter="mio.dominio.aziendale.progetto.MyCustomConverter.convert")
        private BigDecimal totAmount;
    </code>
</pre>

Il risultato all'interno del convertitore generato sarà il seguente:

<pre>
    <code>
        result.setTotAmount(mio.dominio.aziendale.progetto.MyCustomConverter.convert(resultSet));
    </code>
</pre>