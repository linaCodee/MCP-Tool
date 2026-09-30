import { Button, Card, CardActions, CardContent, Chip, Typography } from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import { useNavigate } from 'react-router';
import type { SearchRunResponse, SearchStatus } from '../../../../api/types/searchRun.ts';

interface SearchRunCardProps {
  searchRun: SearchRunResponse;
}

const statusColor: Record<SearchStatus, 'default' | 'info' | 'success' | 'error'> = {
  PENDING: 'default',
  RUNNING: 'info',
  COMPLETED: 'success',
  FAILED: 'error'
};

const SearchRunCard = ({ searchRun }: SearchRunCardProps) => {
  const navigate = useNavigate();

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 1 }}>
        <Typography variant='h6' sx={{ flexGrow: 1 }}>“{searchRun.query}”</Typography>
        <Chip
          label={searchRun.status}
          size='small'
          color={statusColor[searchRun.status]}
          sx={{ alignSelf: 'flex-start' }}
        />
        <Typography variant='body2' color='text.secondary'>
          {searchRun.resultCount ?? 0} resource(s)
        </Typography>
        <Typography variant='caption' color='text.secondary'>
          {new Date(searchRun.createdAt).toLocaleString()}
        </Typography>
      </CardContent>
      <CardActions>
        <Button startIcon={<InfoIcon/>} onClick={() => navigate(`/search-runs/${searchRun.id}`)}>
          Details
        </Button>
      </CardActions>
    </Card>
  );
};

export default SearchRunCard;
