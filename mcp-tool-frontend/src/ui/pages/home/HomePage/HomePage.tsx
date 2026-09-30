import { Box, Container, Typography } from '@mui/material';

/**
 * TODO(student): Turn this into a small dashboard using useStats (which calls
 * GET /api/stats — the same data as the `corpus_stats` MCP tool): total search
 * runs, resources, analyzed resources, donation batches and donated resources.
 */
const HomePage = () => {
  return (
    <Box sx={{ m: 0, p: 0 }}>
      <Container maxWidth='xl' sx={{ mt: 3, py: 3 }}>
        <Typography variant='h4' gutterBottom>
          MCP Tool for doniraj.vezilka.ai 🧵
        </Typography>
        <Typography variant='body1' sx={{ mb: 4 }}>
          This is an MCP (Model Context Protocol) server that searches and
          analyzes a specific Macedonian-language website and donates the
          results to the Vezilka language-preservation platform. Use the Search
          Runs page to collect resources, the Resources page to browse and
          analyze them, the Donations page to submit batches, and the MCP
          Playground to invoke the server's tools directly.
        </Typography>
      </Container>
    </Box>
  );
};

export default HomePage;
