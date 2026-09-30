import {Box, Button, CircularProgress, Link, Typography} from '@mui/material';
import { useParams } from 'react-router';
import ResourceAnalysisPanel from '../../../components/resource/ResourceAnalysisPanel/ResourceAnalysisPanel.tsx';
import useSnackbar from "../../../../hooks/useSnackbar.ts";
import {useCallback, useEffect, useState} from "react";
import type {ResourceResponse} from "../../../../api/types/resource.ts";
import resourceApi from "../../../../api/resourceApi.ts";

/**
 * TODO(student): Show one resource in full: the complete content, the source
 * link, the language confidence, the donation status, and its analysis
 * (resourceApi.findById + the ResourceAnalysisPanel). Add an "Analyze" button
 * that calls resourceApi.analyze.
 */
const ResourceDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const {showSnackbar}=useSnackbar();

  const [resource,setResource] = useState<ResourceResponse | null>(null);
  const [loading,setLoading] = useState<boolean>(false);

  const fetchResource=useCallback(async ()=>{
      if (!id){
          return;
      }

      setLoading(true);

      try {
          const response=await resourceApi.findById(id);
          setResource(response.data);
      } catch (err){
          showSnackbar(err instanceof Error ? err.message : "Failed to load resource",'error');
      } finally {
          setLoading(false);
      }
  },[id,showSnackbar]);

  useEffect(()=>{
      void fetchResource();
  },[fetchResource]);

  const handleAnalyze=async ()=>{
      if (!id){
          return;
      }
      try {
          await resourceApi.analyze(id);
          showSnackbar("Resource analyzed successfully.",'success');
          await fetchResource();
      } catch (err){
          showSnackbar(err instanceof Error ? err.message : 'Failed to analyze resource.','error');
      }
  };
  if (loading){
      return (
          <Box sx={{
              display:'flex',
              justifyContent:'center',
              mt:4
          }}>
              <CircularProgress/>
          </Box>
      );
  }

  if (!resource){
      return (
          <Typography color='text.secondary'>Resource not found</Typography>
      );
  }

  return (
    <Box>
      <Typography variant='h5' gutterBottom>{resource.title}</Typography>
        {resource.sourceUrl && (
            <Link
                href={resource.sourceUrl}
                target='_blank'
                rel='noopener noreferrer'>Source</Link>
        )}
        <Typography variant='body2' sx={{mt:2}}>
            Macedonian confidence:{' '} {resource.macedonianConfidence!=null ? `${Math.round(resource.macedonianConfidence*100)}%` : 'N/A'}
        </Typography>
        <Typography variant='body2'>
            Donation status: {resource.donationBatchId!=null ? `In donation batch #${resource.donationBatchId}` : 'Not donated'}
        </Typography>
        <Typography variant='body1' sx={{mt:3,whiteSpace:'pre-line'}}>{resource.content ?? 'No content available.'}</Typography>
        <Button
            variant='contained'
            sx={{mt:3,mb:3}}
            onClick={()=>void handleAnalyze()}
            disabled={resource.analysis!=null}>
            {resource.analysis ? 'Analyzed' : 'Analyze'}
        </Button>
      <ResourceAnalysisPanel analysis={resource.analysis}/>
    </Box>
  );
};

export default ResourceDetailsPage;
