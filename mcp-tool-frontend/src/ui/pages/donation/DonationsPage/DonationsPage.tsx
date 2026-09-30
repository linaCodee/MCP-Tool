import {Box, Button, CircularProgress, Grid, Paper, Typography} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useState } from 'react';
import useDonations from '../../../../hooks/useDonations.ts';
import DonationBatchCard from '../../../components/donation/DonationBatchCard/DonationBatchCard.tsx';
import SubmitDonationDialog from '../../../components/donation/SubmitDonationDialog/SubmitDonationDialog.tsx';

/**
 * The donation dashboard towards doniraj.vezilka.ai.
 * TODO(student): Implement useDonations, DonationBatchCard and
 * SubmitDonationDialog, plus overall donation statistics.
 */
const DonationsPage = () => {
  const { donations, loading, onCreate, onApprove, onSubmit } = useDonations();

  const [newBatchDialogOpen, setNewBatchDialogOpen] = useState<boolean>(false);
  const total=donations.length;
  const accepted=donations.filter((d)=>d.status==="ACCEPTED").length;
  const submitted=donations.filter((d)=>d.status==="SUBMITTED").length;
  const failed=donations.filter((d)=>d.status==="FAILED").length;
  const rejected=donations.filter((d)=>d.status==="REJECTED").length;

  return (
    <Box>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress/>
        </Box>
      )}
      {!loading &&
       <>
         <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
           <Typography variant='h5'>Donations</Typography>
           <Button variant='contained' startIcon={<AddIcon/>} onClick={() => setNewBatchDialogOpen(true)}>
             New Batch
           </Button>
         </Box>
         <Grid container spacing={2} sx={{mb:3}} >
           <Grid size={{xs:6,md:2.4}}>
             <Paper sx={{p:2}}>
               <Typography color="text.secondary">
                 Total
               </Typography>
               <Typography variant="h5">
                 {total}
               </Typography>
             </Paper>
           </Grid>
           <Grid size={{xs:6,md:2.4}}>
             <Paper sx={{p:2}}>
               <Typography color="text.secondary">
                 Submitted
               </Typography>
               <Typography variant="h5">
                 {submitted}
               </Typography>
             </Paper>
           </Grid>
           <Grid size={{xs:6,md:2.4}}>
             <Paper sx={{p:2}}>
               <Typography color="text.secondary">
                 Accepted
               </Typography>
               <Typography variant="h5">
                 {accepted}
               </Typography>
             </Paper>
           </Grid>
           <Grid size={{xs:6,md:2.4}}>
             <Paper sx={{p:2}}>
               <Typography color="text.secondary">
                 Rejected
               </Typography>
               <Typography variant="h5">
                 {rejected}
               </Typography>
             </Paper>
           </Grid>
           <Grid size={{xs:6,md:2.4}}>
             <Paper sx={{p:2}}>
               <Typography color="text.secondary">
                 Failed
               </Typography>
               <Typography variant="h5">
                 {failed}
               </Typography>
             </Paper>
           </Grid>
         </Grid>
         {donations.length === 0 && (
           <Typography color='text.secondary'>
             No donation batches yet. Group resources into a batch and donate
             them to doniraj.vezilka.ai.
           </Typography>
         )}
         <Grid container spacing={2}>
           {donations.map((batch) => (
             <Grid key={batch.id} size={{ xs: 12, sm: 6, md: 4 }}>
               <DonationBatchCard batch={batch} onApprove={onApprove} onSubmit={onSubmit}/>
             </Grid>
           ))}
         </Grid>
         <SubmitDonationDialog
           open={newBatchDialogOpen}
           onCreate={onCreate}
           onClose={() => setNewBatchDialogOpen(false)}
         />
       </>}
    </Box>
  );
};

export default DonationsPage;
