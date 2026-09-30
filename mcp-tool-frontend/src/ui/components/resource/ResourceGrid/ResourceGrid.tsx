import { Grid } from '@mui/material';
import type { ResourceResponse } from '../../../../api/types/resource.ts';
import ResourceCard from '../ResourceCard/ResourceCard.tsx';

interface ResourceGridProps {
  resources: ResourceResponse[];
  onDelete: (id: number) => Promise<void>;
  onAnalyze: (id: number) => Promise<void>;
}

const ResourceGrid = ({ resources,onDelete,onAnalyze }: ResourceGridProps) => {
  return (
    <Grid container spacing={2}>
      {resources.map((resource) => (
        <Grid key={resource.id} size={{ xs: 12, sm: 6, md: 4 }}>
          <ResourceCard resource={resource} onDelete={onDelete} onAnalyze={onAnalyze} />
        </Grid>
      ))}
    </Grid>
  );
};

export default ResourceGrid;
