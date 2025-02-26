import { Login } from '@mui/icons-material'
import BrowseRecords from 'components/browseRecords/BrowseRecords'
import NotFound from 'components/error/NotFound'
import Import from 'components/import/Import'
import RecordDetails from 'components/recordDetails/RecordDetails'
import NotificationWorklist from 'components/notificationWorklist/NotificationWorklist'
import ReviewLink from 'components/reviewLink/ReviewLink'
import { createBrowserRouter } from 'react-router-dom'
import Root from 'components/shell/Root'
import CustomSearch from 'components/customSearch/CustomSearch'
import SimpleSearch from 'components/search/SimpleSearch'
import SearchResult from 'components/searchResult/SearchResult'

const baseRouter = createBrowserRouter([
  { path: 'login', element: <Login /> },
  {
    path: '/',
    element: <Root />,
    children: [
      {
        path: 'browse-records',
        element: <BrowseRecords />
      },
      {
        path: 'record-details/:uid',
        loader: async ({ params }) => params.uid,
        element: <RecordDetails />
      },
      {
        path: 'record-details/:uid/relink',
        loader: async ({ params }) => params.uid,
        element: <ReviewLink />
      },
      {
        path: 'search',
        children: [
          {
            path: 'simple',
            element: <SimpleSearch />
          },
          {
            path: 'custom',
            element: <CustomSearch />
          }
        ]
      },
      {
        path: 'search-results',
        children: [
          {
            path: 'golden',
            element: (
              <SearchResult isGoldenRecord={true} title="Golden Records Only" />
            )
          },
          {
            path: 'patient',
            element: (
              <SearchResult
                isGoldenRecord={false}
                title="Patient Records Only"
              />
            )
          }
        ]
      },
      {
        path: 'notifications',
        element: <NotificationWorklist />
      },

      {
        path: 'notifications/match-details',
        element: <ReviewLink />
      },
      { path: 'import', element: <Import /> }
    ]
  },
  { element: <NotFound /> }
])

export default baseRouter
