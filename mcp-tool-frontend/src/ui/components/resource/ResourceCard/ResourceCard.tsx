import {Card, Link, CardContent, Typography, CardActions, Button} from '@mui/material';
import type { ResourceResponse } from '../../../../api/types/resource.ts';
import DeleteIcon from '@mui/icons-material/Delete';
import DeleteResourceDialog from "../DeleteResourceDialog/DeleteResourceDialog.tsx";
import {useState} from "react";
import {useNavigate} from "react-router";

interface ResourceCardProps {
  resource: ResourceResponse;
  onDelete:(id:number) => Promise<void>;
  onAnalyze:(id:number) => Promise<void>;
}

/**
 * TODO(student): Show the resource: title, content preview, source link, the
 * Macedonian-language confidence, whether it has been analyzed, and whether it
 * is already part of a donation batch. Add navigation to /resources/{id}, an
 * analyze action and a delete action (useResources().onAnalyze / onDelete).
 */
const ResourceCard = ({ resource,onDelete,onAnalyze }: ResourceCardProps) => {
  const navigate=useNavigate();
  const preview=resource.content && resource.content.length>200 ? `${resource.content.substring(0,200)}...` : resource.content;

  const [deleteOpen, setDeleteOpen ] = useState(false);

  return (
      <>
        <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
          <CardContent sx={{ flexGrow: 1 }}>
            <Typography variant='subtitle2'>{resource.title ?? `Resource #${resource.id}`}</Typography>
            <Typography variant='body2' color='text.secondary'>
              {preview ?? 'No content available.'}
            </Typography>
            {resource.sourceUrl && (
                <Link
                    href={resource.sourceUrl}
                    target='_blank'
                    rel='noopener noreferrer'>Source</Link>
            )}
            <Typography variant='body2'>
              Macedonian confidence:{' '}
              {resource.macedonianConfidence!=null ? `${Math.round(resource.macedonianConfidence*100)}%` : 'N/A'}
            </Typography>
            <Typography variant='body2'>
              {resource.analysis ? 'Analyzed' : 'Not Analyzed'}
            </Typography>
            <Typography variant='body2'>
              {resource.donationBatchId!=null ? 'In donation batch' : 'Not donated'}
            </Typography>
          </CardContent>
          <CardActions sx={{justifyContent: 'space-between'}}>
            <Button onClick={()=>navigate(`/resources/${resource.id}`)}>Details</Button>
            <Button onClick={()=>void onAnalyze(resource.id)} disabled={resource.analysis!=null}>Analyze</Button>
            <Button
                startIcon={<DeleteIcon/>}
                color='error'
                onClick={()=>setDeleteOpen(true)}>Delete</Button>
          </CardActions>
        </Card>
        <DeleteResourceDialog resource={resource} open={deleteOpen} onClose={()=>setDeleteOpen(false)} onDelete={onDelete}/>
      </>
  );
};

export default ResourceCard;
