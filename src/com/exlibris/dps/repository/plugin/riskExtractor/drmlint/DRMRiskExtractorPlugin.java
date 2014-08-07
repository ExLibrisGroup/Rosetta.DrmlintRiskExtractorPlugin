package com.exlibris.dps.repository.plugin.riskExtractor.drmlint;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.exlibris.digitool.common.dnx.DnxDocument;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.risks.RiskExtractor;

public class DRMRiskExtractorPlugin implements RiskExtractor
{

	  private List<Format> formats = new LinkedList<Format>();

	  public DRMRiskExtractorPlugin()
	  {
	    this.formats.add(new PDFFormat());
	  }

	  public void init(Map<String, String> initParameters)
	    throws Exception
	  {
	  }

	  public String getParameter(String paramKey)
	  {
	    return null;
	  }

	  public boolean isAtRisk(DnxDocument dnxDocument, String pathToFile)
	  {
	    boolean drm = false;

	    List<DnxDocumentHelper.FileFormat> list = new DnxDocumentHelper(dnxDocument).getFileFormats();
	    String mimetype = ((list != null) && (list.size() == 1)) ? ((DnxDocumentHelper.FileFormat)list.get(0)).getMimeType() : "";
	    File file = new File(pathToFile);
	    try {
	      for (Format format : this.formats)
	        if (format.canCheck(file, mimetype)) {
	          drm |= format.containsDRM(file);
	          System.out.println("file: " + pathToFile + " with format: " + format.getFormatName() + " was checked for DRM risk");
	        }
	    }
	    catch (Exception e) {
	    	System.out.println("failed to run DRM risk extractor on file: " + pathToFile + " with error: " + e.getMessage());
	      return false;
	    }
	    return drm;
	  }
	}