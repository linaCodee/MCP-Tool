import {Button, Card, CardContent, Chip, Typography} from '@mui/material';
import type { DonationBatchResponse } from '../../../../api/types/donation.ts';

interface DonationBatchCardProps {
  batch: DonationBatchResponse;
  onApprove:(id:number)=>Promise<void>,
  onSubmit:(id:number)=>Promise<void>
}

/**
 * TODO(student): Show the batch: number of resources, status, the Vezilka
 * reference once submitted, and approve/submit actions
 * (useDonations().onApprove / onSubmit) enabled according to the status.
 */
const DonationBatchCard = ({ batch, onSubmit, onApprove }: DonationBatchCardProps) => {
  return (
    <Card>
      <CardContent>
        <Typography variant='h6'>Batch #{batch.id}</Typography>
        <Chip label={batch.status} size='small'/>
        <Typography variant='body2' color='text.secondary' sx={{ mt: 1 }}>
          Resources: {batch.resourceCount}
        </Typography>
        {batch.resourceIds.length>0 && (
            <Typography variant='body2' color='text.secondary'>Resource IDs: {batch.resourceIds.join(', ')}</Typography>
        )}
        {batch.vezilkaReference && (
            <Typography variant='body2' color='text.secondary' >Vezilka reference: {batch.vezilkaReference}</Typography>
        )}
        {batch.submittedAt && (
            <Typography variant='caption' color='text.secondary'>Submitted: {new Date(batch.submittedAt).toLocaleString()}</Typography>
        )}
        {batch.status==='DRAFT' && (
            <Button size='small' variant='contained' onClick={()=>onApprove(batch.id)}>Approve</Button>
        )}
        {batch.status==='APPROVED' && (
            <Button size='small' variant='contained' onClick={()=>onSubmit(batch.id)}>Submit</Button>
        )}
      </CardContent>
    </Card>
  );
};

export default DonationBatchCard;
