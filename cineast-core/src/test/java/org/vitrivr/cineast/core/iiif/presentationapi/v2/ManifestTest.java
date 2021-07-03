package org.vitrivr.cineast.core.iiif.presentationapi.v2;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Manifest;

/**
 * @author singaltanmay
 * @version 1.0
 * @created 23.06.21
 */
public class ManifestTest {

  private static final String JSON_RESPONSE = "{\n"
      + "  \"@context\": \"http://iiif.io/api/presentation/2/context.json\",\n"
      + "  \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/manifest.json\",\n"
      + "  \"@type\": \"sc:Manifest\",\n"
      + "  \"label\": \"Cambridge, Corpus Christi College, MS 200: Baldwin of Ford OCist, De sacramento altaris.\",\n"
      + "  \"description\": \"The De sacramento altaris by Baldwin of Ford OCist, archbishop of Canterbury (d. 1190), contained in CCCC MS 200, was written in the period 1170-80. This copy was produced after he became archbishop in 1184 because an historiated initial at the beginning of the text shows him writing and wearing a mitre. The text discusses the biblical sources of the eucharist, foreshadowed in the paschal lamb and manna of the Old Testament, and defined in the New Testament writings of Saints Matthew, John and Paul. The book belonged to Christ Church cathedral priory, Canterbury.\",\n"
      + "  \"attribution\": \"Images courtesy of The Parker Library, Corpus Christi College, Cambridge. Licensed under a Creative Commons Attribution-NonCommercial 4.0 International License. For higher resolution images suitable for scholarly or commercial publication, either in print or in an electronic format, please contact the Parker Library directly at parker-library@corpus.cam.ac.uk\",\n"
      + "  \"logo\": {\n"
      + "    \"@id\": \"https://stacks.stanford.edu/image/iiif/wy534zh7137%2FSULAIR_rosette/full/400,/0/default.jpg\",\n"
      + "    \"service\": {\n"
      + "      \"@context\": \"http://iiif.io/api/image/2/context.json\",\n"
      + "      \"@id\": \"https://stacks.stanford.edu/image/iiif/wy534zh7137%2FSULAIR_rosette\",\n"
      + "      \"profile\": \"http://iiif.io/api/image/2/level1.json\"\n"
      + "    }\n"
      + "  },\n"
      + "  \"seeAlso\": {\n"
      + "    \"@id\": \"https://purl.stanford.edu/bg021sq9590.mods\",\n"
      + "    \"format\": \"application/mods+xml\"\n"
      + "  },\n"
      + "  \"viewingHint\": \"paged\",\n"
      + "  \"metadata\": [\n"
      + "    {\n"
      + "      \"label\": \"Title\",\n"
      + "      \"value\": \"Baldwin of Ford OCist, De sacramento altaris\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Title\",\n"
      + "      \"value\": \"Baldewinus de sacramento altaris\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Contributor\",\n"
      + "      \"value\": \"Christ Church, Canterbury (originator)\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Type\",\n"
      + "      \"value\": \"Text\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Language\",\n"
      + "      \"value\": \"Latin.\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Format\",\n"
      + "      \"value\": \"electronic\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Format\",\n"
      + "      \"value\": \"image/tif\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Subject\",\n"
      + "      \"value\": \"Manuscripts\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Coverage\",\n"
      + "      \"value\": \"Great Britain\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Subject\",\n"
      + "      \"value\": \"Manuscripts--Great Britain\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Identifier\",\n"
      + "      \"value\": \"http://parkerweb.stanford.edu/\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Identifier\",\n"
      + "      \"value\": \"CCC200\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Identifier\",\n"
      + "      \"value\": \"Stanley_V. 3\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Identifier\",\n"
      + "      \"value\": \"TJames_302\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"Relation\",\n"
      + "      \"value\": \"Parker Manuscripts\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"label\": \"PublishDate\",\n"
      + "      \"value\": \"2017-03-28T17:00:21Z\"\n"
      + "    }\n"
      + "  ],\n"
      + "  \"thumbnail\": {\n"
      + "    \"@id\": \"https://stacks.stanford.edu/image/iiif/bg021sq9590%2F200_1_TC_46/full/!400,400/0/default.jpg\",\n"
      + "    \"@type\": \"dctypes:Image\",\n"
      + "    \"format\": \"image/jpeg\",\n"
      + "    \"service\": {\n"
      + "      \"@context\": \"http://iiif.io/api/image/2/context.json\",\n"
      + "      \"@id\": \"https://stacks.stanford.edu/image/iiif/bg021sq9590%2F200_1_TC_46\",\n"
      + "      \"profile\": \"http://iiif.io/api/image/2/level1.json\"\n"
      + "    }\n"
      + "  },\n"
      + "  \"sequences\": [\n"
      + "    {\n"
      + "      \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/normal\",\n"
      + "      \"@type\": \"sc:Sequence\",\n"
      + "      \"label\": \"Current page order\",\n"
      + "      \"canvases\": [\n"
      + "        {\n"
      + "          \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/canvas/canvas-1\",\n"
      + "          \"@type\": \"sc:Canvas\",\n"
      + "          \"label\": \"f. ar\",\n"
      + "          \"height\": 9198,\n"
      + "          \"width\": 6226,\n"
      + "          \"images\": [\n"
      + "            {\n"
      + "              \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/imageanno/anno-1\",\n"
      + "              \"@type\": \"oa:Annotation\",\n"
      + "              \"motivation\": \"sc:painting\",\n"
      + "              \"resource\": {\n"
      + "                \"@id\": \"https://stacks.stanford.edu/image/iiif/bg021sq9590%2F200_a_R_TC_46/full/full/0/default.jpg\",\n"
      + "                \"@type\": \"dctypes:Image\",\n"
      + "                \"format\": \"image/jpeg\",\n"
      + "                \"height\": 9198,\n"
      + "                \"width\": 6226,\n"
      + "                \"service\": {\n"
      + "                  \"@id\": \"https://stacks.stanford.edu/image/iiif/bg021sq9590%2F200_a_R_TC_46\",\n"
      + "                  \"@context\": \"http://iiif.io/api/image/2/context.json\",\n"
      + "                  \"profile\": \"http://iiif.io/api/image/2/level1.json\"\n"
      + "                }\n"
      + "              },\n"
      + "              \"on\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/canvas/canvas-1\"\n"
      + "            }\n"
      + "          ]\n"
      + "        },\n"
      + "        {\n"
      + "          \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/canvas/canvas-222\",\n"
      + "          \"@type\": \"sc:Canvas\",\n"
      + "          \"label\": \"f. ev\",\n"
      + "          \"height\": 9150,\n"
      + "          \"width\": 6262,\n"
      + "          \"images\": [\n"
      + "            {\n"
      + "              \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/imageanno/anno-222\",\n"
      + "              \"@type\": \"oa:Annotation\",\n"
      + "              \"motivation\": \"sc:painting\",\n"
      + "              \"resource\": {\n"
      + "                \"@id\": \"https://stacks.stanford.edu/image/iiif/bg021sq9590%2F200_e_V_TC_46/full/full/0/default.jpg\",\n"
      + "                \"@type\": \"dctypes:Image\",\n"
      + "                \"format\": \"image/jpeg\",\n"
      + "                \"height\": 9150,\n"
      + "                \"width\": 6262,\n"
      + "                \"service\": {\n"
      + "                  \"@id\": \"https://stacks.stanford.edu/image/iiif/bg021sq9590%2F200_e_V_TC_46\",\n"
      + "                  \"@context\": \"http://iiif.io/api/image/2/context.json\",\n"
      + "                  \"profile\": \"http://iiif.io/api/image/2/level1.json\"\n"
      + "                }\n"
      + "              },\n"
      + "              \"on\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/canvas/canvas-222\"\n"
      + "            }\n"
      + "          ]\n"
      + "        }\n"
      + "      ]\n"
      + "    }\n"
      + "  ],\n"
      + "  \"structures\": [\n"
      + "    {\n"
      + "      \"@type\": \"sc:Range\",\n"
      + "      \"label\": \"Table of Contents\",\n"
      + "      \"viewingHint\": \"top\",\n"
      + "      \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/range/r0\",\n"
      + "      \"ranges\": [\n"
      + "        \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/range/r1\"\n"
      + "      ]\n"
      + "    },\n"
      + "    {\n"
      + "      \"@id\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/range/r1\",\n"
      + "      \"@type\": \"sc:Range\",\n"
      + "      \"label\": \"Baldwin of Ford OCist, De sacramento altaris\",\n"
      + "      \"within\": \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/range/r0\",\n"
      + "      \"canvases\": [\n"
      + "        \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/canvas/canvas-9\",\n"
      + "        \"https://dms-data.stanford.edu/data/manifests/Parker/bg021sq9590/canvas/canvas-213\"\n"
      + "      ]\n"
      + "    }\n"
      + "  ]\n"
      + "}\n";

  @Test
  public void parsingTest() throws JsonProcessingException {
    Manifest manifest = new ObjectMapper().readValue(JSON_RESPONSE, Manifest.class);
    assertNotNull(manifest);
  }

}
