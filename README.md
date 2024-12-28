# Browserstack Assignment (TestNG-Selenium)

## Description
This assignment highlights the use of Selenium and BrowserStack for web scraping, API integration, and text processing. It involves collecting article headlines from the "Opinion" section of the El País website, translating them, and performing cross-browser testing to ensure functionality. Additonally, we also save cover photo images and print article content on console.

## Prerequisites
- Java should be installed in system (8 and above).
- BrowserStack account credentials (username and access key).

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/linpaws1364/web-scrap-selenium-browserstack-assignment.git
   cd web-scrap-selenium-browserstack
   ```

2. Install mvn dependencies:
   ```bash
   mvn clean install -Dmaven.test.skip=true
   ```

3. Configure browserstack.yml
   ```
    userName: browserstack_username
    accessKey: browserstack_accesskey

    framework: testng

    platforms:
      - os: Windows
        osVersion: 10
        browserName: Chrome
        browserVersion: 120.0
      - os: Windows
        osVersion: 10
        browserName: Firefox
        browserVersion: 120.0
      - os: Windows
        browserName: Edge
        osVersion: 11
        browserVersion: 125.0
      - deviceName: Google Pixel 9 Pro XL
        browserName: chrome
        osVersion: 15.0
        realMobile: true
        browserVersion: latest
      - deviceName: Galaxy S23
        browserName: chromium
        osVersion: 13
        deviceOrientation: portrait
        realMobile: true
        browserVersion: latest

    parallelsPerPlatform: 5

    browserstackLocal: false

    buildName: Elpais_assignment
    projectName: BrowserStack Assignment
    buildIdentifier: ${BUILD_NUMBER}

    testObservability: true
    networkLogs: false
    consoleLogs: info

    percy: false
    percyCaptureMode: auto
   ```
   Make sure the capabilties, parallel count and required logs are enabled as per the requirement. Here we are running on 5 threads across multiple browsers on multiple OS.

4. Add browserstack specific flags in pom.xml

  ```
    <groupId>com.browserstack</groupId>
    <artifactId>testng-browserstack</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>

    <dependency>
            <groupId>com.browserstack</groupId>
            <artifactId>browserstack-java-sdk</artifactId>
            <version>LATEST</version>
            <scope>compile</scope>
    </dependency>


    <profile>
            <id>browserstack-test</id>
            <build>
                <plugins>
                   <plugin>
               <artifactId>maven-dependency-plugin</artifactId>
                 <executions>
                   <execution>
                     <id>getClasspathFilenames</id>
                       <goals>
                         <goal>properties</goal>
                       </goals>
                   </execution>
                 </executions>
            </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-surefire-plugin</artifactId>
                        <configuration>
                            <suiteXmlFiles>
                                <suiteXmlFile>xml/testng_win.xml</suiteXmlFile>
                            </suiteXmlFiles>
                            <argLine>
                                -javaagent:${com.browserstack:browserstack-java-sdk:jar}
                            </argLine>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
    </profile>
  ```
  
  The dependency is mandatory to add. The configuration of suiteXmlFiles can be directly implemnted under maven-surefire-plugin, however to keep the suite generic and reproducible for other use cases, we have implemnted browserstack specific execution as a seprate profile. 


## Usage

1. Run the script:
   ```bash
   mvn test -P browserstack-test
   ```

2. Steps to perform:
   - Prints title and content of first 5 articles present in in the "Opinion" section.
   - Downloads the cover images of the articles (if available) inside ArticleImages folder in the root.
   - Translates the titles of the articles from Spanish to English and prints in the console.
   - Prints repeated words present in the translated titles along with the count of the same.

3. Sample Output:
  ```

    ------------------Article Starts------------------


    Title: Feijóo y la confusión


    Content: El líder popular recurre a la hipérbole en sus críticas al Gobierno y obvia las enormes competencias autonómicas que dependen de su partido


    ------------------Article Ends------------------


    ------------------Article Starts------------------


    Title: México mira al norte


    Content: El regreso de Trump a la Casa Blanca no puede ir acompañado de amenazas hacia su vecino del sur


    ------------------Article Ends------------------


    ------------------Article Starts------------------


    Title: Las mentiras del año nuevo


    Content: Toca volver a engañarme y pensar que puedo ser otra a partir del 1 de enero


    ------------------Article Ends------------------


    ------------------Article Starts------------------


    Title: Barco lleno, barco vacío


    Content: El espacio más natural de la fraternidad es la familia. No la elegimos, pero es que no elegimos casi nada de lo que más nos define


    ------------------Article Ends------------------


    ------------------Article Starts------------------


    Title: ‘Las tres fronteras’


    Content: Este proyecto fotográfico documenta los límites entre Malí, Níger y Burkina Faso, epicentro del conflicto que devasta el Sahel. El estallido de la insurgencia yihadista en 2012, alimentada por la pobreza, la injusticia y la debilidad de los gobiernos de la región, abrió las puertas a una violencia que hoy ejercen tanto grupos armados como ejércitos y mercenarios y que sufren, especialmente, los civiles. Con unos 40.000 muertos y cuatro millones de refugiados y desplazados de sus hogares en una región ya castigada por la falta de recursos y el cambio climático, se trata de una de las crisis humanitarias más duras del mundo, casi siempre opacada por otras guerras


    ------------------Article Ends------------------
    titles:[Feijóo y la confusión, México mira al norte, Las mentiras del año nuevo, Barco lleno, barco vacío, ‘Las tres fronteras’]
    Translated Title: Feij�o and confusing it


    Translated Title: Mexico looks north


    Translated Title: The lies of the new year


    Translated Title: Full ship, empty boat


    Translated Title: ‘The three borders’


    Word: the, Count: 3
  ```

## Breakdown of the steps performed in the test

1. **Visit the website El País:**
   - The website is loaded up on different browsers.
   - Cookies are accepted, the prompt comes up in two different ways, handling has been added for both.

2. **Scrape Articles from the Opinion Section:**
   - Navigates to the Opinion section of the website.
   - Fetches the first five articles in this section.
   - Prints the title and content of each article in Spanish.
   - If available, downloads and saves the cover image of each article inside ArticleImages folder in your local machine.

3. **Translate Article Headers:**
   - Uses a translation API (e.g., Google Translate API or Rapid Translate Multi Traduction API).
   - Translates the title of each article to English and prints the translated headers.

4. **Analyze Translated Headers:**
   - Identifies repeated words (occurring more than twice) across all translated headers combined.
   - Prints each repeated word along with the count of its occurrences.

## Testing in BrowserStack

We have executed the test cases across 5 different browsers on different OS (desktop and mobile included). Please refer to below link to go through the execution.

Build link - https://automate.browserstack.com/dashboard/v2/public-build/cm8yTnhiaWsrUVM0MWRnQW1kblNCdTIxL3lBbjA1dlFHZzdUMTdwUEtLWEhia1VhNWZncjNHTDFNU1ljdDdJWkdhSHJoQkRKZERKWWNmWVFCWXVjTXc9PS0tL1dKV0JlRnU3YVlRU3BSN2dQQ1NQZz09--74646157e8e3ea601a652aff82a4f83a313623a6
