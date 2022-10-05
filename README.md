# NiFi RSS Processor

> Worked with NiFi 1.15.2 successfully!

## Overview

This project is a custom [NiFi](https://nifi.apache.org/) processor that extracts fields
from a Flow File with content that is valid RSS XML. The processing is done by the
[ROME](https://rometools.github.io/rome/) Java framework for RSS and Atom feeds.

The processor creates a JSON object with the following fields - with the field name
in parenthesis:

- Publication date (publicationDate)
- Author (author)
- Item title (title)
- Item description (description)
- Item url (url)
- Title of feed (feedTitle)
- Url of feed (feedUrl)

## Processor Work Flow

The expected input to this Processor is a NiFi flow file where the content is an XML
document that can be processed by the ROME RSS framework.

The output from the processor is one (or more) flow files:

- The original flow file will pass to either the _SUCCESS_ or _FAILURE_ relationship
  depending on whether ROME was able to parse the XML input.
- A new flow file will be created for each Item detected in the RSS XML content. The
  format of the content of the new flow files is described above.

## Using the Processor

This project was generated from the NiFi maven archetype and the build process follows
the approach for a project created from the archetype.

### Building NiFi NAR

First step is to clone the repository.

```
git clone https://github.com/gm-ram/nifirssbundle.git
```

In the root directory of the project run:

```
mvn clean install
```

If the build is successful there will be a **nar** file in the folder _nifi-rss-nar/target_.

### Building NiFi NAR with Docker

First step is to clone the repository.

```
git clone https://github.com/gm-ram/nifirssbundle.git
```

In the root directory of the project run:

```bash
docker run -it --rm --name my-maven-project -v ${pwd}:/usr/src/mymaven -w /usr/src/mymaven maven:3.3-jdk-8 mvn clean install
```

If the build is successful there will be a **nar** file in the folder _nifi-rss-nar/target_.

### Adding to NiFi

The **nar** file created after the build process needs to be copied into the _lib_ folder of the NiFi
installation.

The final stage is to restart NiFi so that it picks up the new Processor.

## License

- Apache NiFi - Apache License, Version 2.0
- ROME - Apache License, Version 2.0
